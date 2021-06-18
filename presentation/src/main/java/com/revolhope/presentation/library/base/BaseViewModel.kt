package com.revolhope.presentation.library.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revolhope.domain.common.extensions.collectOnMain
import com.revolhope.domain.common.extensions.logVerbose
import com.revolhope.domain.common.model.State
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    companion object {
        private const val JOB_EXECUTED_AGAIN_JOB_CANCEL_CAUSE = "Job with id: %d is currently working " +
                "and a new call have been done. Cancelling working Job and starting new one."
        private const val NON_LOADING_STATE_JOB_CANCEL_CAUSE = "Job with id: %d have been cancelled " +
                "due to State object different of State.Loading have been collected."
        private const val VIEW_MODEL_CLEARED_JOB_CANCEL_CAUSE = "ViewModel (%s) have been cleared and" +
                "as a consequence all current jobs have been forced to be cancelled"
    }

    private val mainJob: Job = Job()
    private val jobPool: MutableMap<Int, Job> = mutableMapOf()

    protected val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    protected val _errorResLiveData = MutableLiveData<Int>()
    val errorResLiveData: LiveData<Int> get() = _errorResLiveData

    protected val _loadingLiveData = MutableLiveData<String?>()
    val loadingLiveData: LiveData<String?> get() = _loadingLiveData

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mainJob

    override fun onCleared() {
        super.onCleared()
        jobPool.keys.forEach { cancelJobById(it, VIEW_MODEL_CLEARED_JOB_CANCEL_CAUSE) }
        mainJob.cancel()
    }

    private fun cancelJobById(jobId: Int, cause: String): Boolean =
        jobPool[jobId]?.let {
            it.cancel()
            jobPool.remove(jobId)
            true
        } ?: false

    internal fun <T> collectFlow(
        dispatcher: CoroutineContext = Dispatchers.IO,
        loadingMessage: String? = null,
        onSuccessLiveData: MutableLiveData<T>,
        flowTask: suspend () -> Flow<State<T>>
    ): Int =
        this.collectFlow(
            dispatcher = dispatcher,
            loadingMessage = loadingMessage,
            flowTask = flowTask,
            onSuccessCollected = onSuccessLiveData::setValue
        )

    internal fun <T> collectFlow(
        dispatcher: CoroutineContext = Dispatchers.IO,
        loadingMessage: String? = null,
        flowTask: suspend () -> Flow<State<T>>,
        onSuccessCollected: (data: T) -> Unit,
        onLoadingCollected: ((String?) -> Unit)? = null,
        onErrorCollected: ((isResource: Boolean, message: String?) -> Unit)? = null
    ): Int {
        // Cancel Job in case of being active
        val jobId = flowTask.hashCode()
        cancelJobById(jobId, JOB_EXECUTED_AGAIN_JOB_CANCEL_CAUSE)

        // Create new Job which executes flowTask
        viewModelLaunch(dispatcher) {
            // Collect flow data on Dispatchers.Main
            flowTask.invoke().collectOnMain {
                // TODO: Remove Log!
                if (it is State.Loading) logVerbose("TEEEST", "State.LOADING")
                // Manage collected State
                handleState(
                    state = it,
                    onSuccess = onSuccessCollected,
                    onLoading = onLoadingCollected,
                    onLoadingFeedbackMessage = loadingMessage,
                    onError = onErrorCollected
                )
                // Kill job in case of State != State.Loading
                if (it !is State.Loading) cancelJobById(jobId, NON_LOADING_STATE_JOB_CANCEL_CAUSE)
            }
        }.also { jobPool[jobId] = it }
        return jobId
    }

    internal fun viewModelLaunch(
        dispatcher: CoroutineContext,
        block: suspend () -> Unit
    ): Job =
        viewModelScope.launch {
            withContext(dispatcher) {
                block.invoke()
            }
        }

    internal fun <T> handleState(
        state: State<T>,
        onSuccess: (data: T) -> Unit,
        onLoading: ((String?) -> Unit)? = null,
        onLoadingFeedbackMessage: String? = null,
        onError: ((isResource: Boolean, message: String?) -> Unit)? = null
    ): Boolean {
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
