package com.revolhope.presentation.library.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revolhope.domain.common.model.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private val job = Job()

    protected val _errorLiveData = MutableLiveData<String>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    protected fun <T> handleState(
        state: State<T>,
        onSuccess: (data: T) -> Unit,
        onError: ((message: String?) -> Unit)? = null
    ) {
        when (state) {
            is State.Success -> onSuccess.invoke(state.data)
            is State.Error -> {
                onError?.invoke(state.message) ?: state.message?.let(_errorLiveData::setValue)
            }
        }
    }
}