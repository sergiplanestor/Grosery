package com.revolhope.data.common

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.revolhope.domain.common.extensions.asStateFlow
import com.revolhope.domain.common.model.ErrorMessage
import com.revolhope.domain.common.model.NetworkError
import com.revolhope.domain.common.model.State
import com.revolhope.domain.common.model.asStateError
import kotlinx.coroutines.flow.Flow

abstract class BaseRepositoryImpl {

    internal suspend inline fun <T> stateful(
        isLoadingEnabled: Boolean = true,
        crossinline block: suspend () -> Flow<T>
    ): Flow<State<T>> =
        block.asStateFlow(isLoadingEnabled, ::catchDelegate)

    internal suspend inline fun <T> stateful(
        isLoadingEnabled: Boolean = true,
        crossinline block: suspend () -> Flow<T>,
        noinline onMapTransformation: ((T) -> State<T>)? = null
    ): Flow<State<T>> =
        block.asStateFlow(isLoadingEnabled, ::catchDelegate, onMapTransformation)

    internal fun <T : Throwable> catchDelegate(e: T): State.Error =
        e.asStateError {
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
            ErrorMessage(
                message = message,
                messageRes = messageRes,
            ) to errorCode
        }
}
