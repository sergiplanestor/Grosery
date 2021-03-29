package com.revolhope.data.common

import com.revolhope.domain.common.model.State
import java.lang.Exception

abstract class BaseRepositoryImpl {

    protected inline fun <T> launchStateful(crossinline action: () -> T): State<T> =
        try {
            State.Success(data = action.invoke())
        } catch (e: Exception) {
            State.Error(e.message, e)
        }
}