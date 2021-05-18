package com.revolhope.data.common

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.revolhope.data.feature.storage.network.FirebaseError
import com.revolhope.domain.common.model.NetworkError
import com.revolhope.domain.common.model.State
import java.lang.Exception

abstract class BaseRepositoryImpl {

    protected suspend inline fun <T> launchStateful(crossinline action: suspend () -> T): State<T> =
        try {
            State.Success(data = action.invoke())
        } catch (e: Exception) {
            var message: String? = null
            var messageRes: Int? = null
            var errorCode: String? = null
            var throwable: Throwable? = null
            when (e) {
                is FirebaseException -> {
                    when (e) {
                        is FirebaseAuthException -> {
                            messageRes = NetworkError.errorMessage(e.errorCode)
                            errorCode = e.errorCode
                            throwable = e
                        }
                        is FirebaseNetworkException -> {
                            messageRes = NetworkError.errorMessage(NetworkError.FIREBASE_TIMEOUT)
                            errorCode = NetworkError.FIREBASE_TIMEOUT
                            throwable = e
                        }
                        else -> {
                            message = e.message
                            messageRes = null
                            errorCode = null
                            throwable = e
                        }
                    }
                }
                else -> {
                    message = e.message
                    messageRes = null
                    errorCode = null
                    throwable = e
                }
            }
            State.Error(
                message = message,
                messageRes = messageRes,
                errorCode = errorCode,
                throwable = throwable
            )
        }
}
