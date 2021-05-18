package com.revolhope.presentation.library.base

import androidx.lifecycle.LiveData
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
    val errorLiveData: LiveData<String> get() = _errorLiveData

    protected val _errorResLiveData = MutableLiveData<Int>()
    val errorResLiveData: LiveData<Int> get() = _errorResLiveData

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    protected fun <T> handleState(
        state: State<T>,
        onSuccess: (data: T) -> Unit,
        onError: ((isResource: Boolean, message: String?) -> Unit)? = null
    ) {
        when (state) {
            is State.Success -> onSuccess.invoke(state.data)
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
    }
}
