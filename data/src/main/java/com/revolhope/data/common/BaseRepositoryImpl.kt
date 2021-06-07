package com.revolhope.data.common

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.revolhope.domain.common.model.NetworkError
import com.revolhope.domain.common.model.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseRepositoryImpl {

    protected suspend inline fun <T> runStatefulFlow(
        emitLoadingState: Boolean = true,
        crossinline action: suspend () -> T
    ): Flow<State<T>> =
        flow {
            if(emitLoadingState) emit(State.Loading)
            val state = launchStateful(action)
            emit(state)
        }

    protected suspend inline fun <T> launchStateful(crossinline action: suspend () -> T): State<T> =
        try {
            State.Success(data = action.invoke())
        } catch (e: Exception) {
            var message: String? = null
            var messageRes: Int? = null
            var errorCode: String? = null
            when (e) {
                is FirebaseException -> {
                    when (e) {
                        is FirebaseAuthException -> {
                            messageRes = NetworkError.errorMessage(e.errorCode)
                            errorCode = e.errorCode
                        }
                        is FirebaseNetworkException -> {
                            messageRes = NetworkError.errorMessage(NetworkError.FIREBASE_TIMEOUT)
                            errorCode = NetworkError.FIREBASE_TIMEOUT
                        }
                        else -> {
                            message = e.message
                        }
                    }
                }
                else -> {
                    message = e.message
                }
            }
            State.Error(
                message = message,
                messageRes = messageRes,
                errorCode = errorCode,
                throwable = e
            )
        }
}
