package com.revolhope.data.common

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.revolhope.domain.common.model.State
import java.lang.Exception

abstract class BaseRepositoryImpl {

    protected suspend inline fun <T> launchStateful(crossinline action: suspend () -> T): State<T> =
        try {
            State.Success(data = action.invoke())
        } catch (e: Exception) {
            State.Error(e.message, throwable = e)
        }
}
