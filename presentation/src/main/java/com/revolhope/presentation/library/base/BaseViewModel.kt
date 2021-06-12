package com.revolhope.presentation.library.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revolhope.domain.common.model.State
import com.revolhope.presentation.library.extensions.collectOnMainContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private val job = Job()

    protected val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    protected val _errorResLiveData = MutableLiveData<Int>()
    val errorResLiveData: LiveData<Int> get() = _errorResLiveData

    protected val _loadingLiveData = MutableLiveData<String?>()
    val loadingLiveData: LiveData<String?> get() = _loadingLiveData

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    internal fun <T> collectOn(
        dispatcher: CoroutineContext = Dispatchers.IO,
        loadingMessage: String? = null,
        task: suspend () -> Flow<State<T>>,
        onTaskSuccess: (data: T) -> Unit,
        onTaskLoading: ((String?) -> Unit)? = null,
        onTaskFailure: ((isResource: Boolean, message: String?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            withContext(dispatcher) {
                task.invoke().collectOnMainContext {
                    handleState(
                        state = it,
                        onSuccess = onTaskSuccess,
                        onLoading = onTaskLoading,
                        onLoadingFeedbackMessage = loadingMessage,
                        onError = onTaskFailure
                    )
                }
            }
        }
    }

    fun <T> handleState(
        state: State<T>,
        onSuccess: (data: T) -> Unit,
        onLoading: ((String?) -> Unit)? = null,
        onLoadingFeedbackMessage: String? = null,
        onError: ((isResource: Boolean, message: String?) -> Unit)? = null
    ) : Boolean {
        when (state) {
            is State.Success -> onSuccess.invoke(state.data)
            is State.Loading -> {
                if (onLoading != null) {
                    onLoading.invoke(onLoadingFeedbackMessage)
                } else {
                    _loadingLiveData.value = onLoadingFeedbackMessage
                }
            }
            is State.Error -> {
                when {
                    onError != null -> {
                        onError.invoke(state.isErrorMessageResource, state.errorMessageOrEmpty)
                    }
                    state.isErrorMessageResource -> {
                        state.errorMessageOrEmpty.toIntOrNull()?.let(_errorResLiveData::setValue)
                    }
                    else /* onError == null && !isErrorMessageResource */ -> {
                        _errorLiveData.value = state.errorMessageOrEmpty
                    }
                }
            }
        }

        return state is State.Success
    }
}
