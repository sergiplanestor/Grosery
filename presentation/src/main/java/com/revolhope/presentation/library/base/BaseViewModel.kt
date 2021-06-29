package com.revolhope.presentation.library.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revolhope.domain.common.base.UseCase
import com.revolhope.domain.common.model.State
import com.revolhope.presentation.library.component.loader.LoaderData
import com.revolhope.presentation.library.component.loader.LoadingMessageModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private val mainJob: Job = Job()

    protected val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    protected val _errorResLiveData = MutableLiveData<Int>()
    val errorResLiveData: LiveData<Int> get() = _errorResLiveData

    protected val _loadingLiveData = MutableLiveData<LoaderData>()
    val loadingLiveData: LiveData<LoaderData> get() = _loadingLiveData

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mainJob

    override fun onCleared() {
        super.onCleared()
        mainJob.cancel()
    }

    internal open fun handleLoadingState(show: Boolean, messageModel: LoadingMessageModel?) {
        _loadingLiveData.value = show to messageModel
    }

    private fun launchOnViewModelScope(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    internal fun <R> launch(
        dispatcher: CoroutineContext = Dispatchers.IO,
        task: suspend () -> R
    ) {
        launchAndAlso(
            dispatcher = dispatcher,
            task = task
        )
    }

    internal fun <R> launchAndAlso(
        dispatcher: CoroutineContext = Dispatchers.IO,
        task: suspend () -> R,
        also: R.() -> Unit = {}
    ) {
        launchOnViewModelScope {
            withContext(dispatcher) {
                task.invoke()
            }.also(also)
        }
    }

    internal fun <REQ, RES, USE_CASE : UseCase<REQ, RES>> invokeUseCase(
        dispatcher: CoroutineContext = Dispatchers.IO,
        useCase: USE_CASE,
        request: REQ,
        loadingModel: LoadingMessageModel? = null,
        onSuccessLiveData: MutableLiveData<RES>
    ) =
        invokeUseCase(
            dispatcher = dispatcher,
            useCase = useCase,
            request = request,
            loadingModel = loadingModel,
            onSuccessCollected = onSuccessLiveData::setValue
        )

    internal fun <REQ, RES, USE_CASE : UseCase<REQ, RES>> invokeUseCase(
        dispatcher: CoroutineContext = Dispatchers.IO,
        useCase: USE_CASE,
        request: REQ,
        loadingModel: LoadingMessageModel? = null,
        onSuccessCollected: (data: RES) -> Unit,
        onLoadingCollected: ((Boolean, LoadingMessageModel?) -> Unit)? = null,
        onFailureCollected: ((isResource: Boolean, message: String?) -> Unit)? = null
    ) =
        launch(
            dispatcher = dispatcher,
            task = {
                useCase
                    .invoke(viewModelScope, request)
                    .collectState(
                        loadingModel = loadingModel,
                        onSuccessCollected = onSuccessCollected,
                        onLoadingCollected = onLoadingCollected ?: ::handleLoadingState,
                        onFailureCollected = onFailureCollected
                    )
            }
        )

    internal suspend fun <T> Flow<State<T>>.collectState(
        loadingModel: LoadingMessageModel? = null,
        onSuccessCollected: (data: T) -> Unit,
        onLoadingCollected: ((Boolean, LoadingMessageModel?) -> Unit)? = null,
        onFailureCollected: ((isResource: Boolean, message: String?) -> Unit)? = null
    ) {
        val flow = this
        withContext(Dispatchers.Main) {
            flow.collect { state ->
                handleState(
                    state = state,
                    loadingModel = loadingModel,
                    onSuccess = onSuccessCollected,
                    onLoading = onLoadingCollected,
                    onFailure = onFailureCollected
                )
            }
        }
    }

    internal fun <T> handleState(
        state: State<T>,
        loadingModel: LoadingMessageModel? = null,
        onSuccess: (data: T) -> Unit,
        onLoading: ((show: Boolean, message: LoadingMessageModel?) -> Unit)? = null,
        onFailure: ((isResource: Boolean, message: String?) -> Unit)? = null
    ): Boolean {
        when (state) {
            is State.Success -> onSuccess.invoke(state.data)
            is State.Loading -> onLoading?.invoke(true, loadingModel)
            is State.Error -> {
                when {
                    onFailure != null -> {
                        onFailure.invoke(state.isErrorMessageResource, state.errorMessageOrEmpty)
                    }
                    state.isErrorMessageResource -> {
                        state.errorMessageOrEmpty.toIntOrNull()?.let(_errorResLiveData::setValue)
                    }
                    else /* onError == null && !isErrorMessageResource */ -> {
                        _errorLiveData.value = state.errorMessageOrEmpty
                    }
                }
            }
        }.also { if (state !is State.Loading) onLoading?.invoke(false, null) }

        return state is State.Success
    }
}
