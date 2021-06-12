package com.revolhope.data.common

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.revolhope.domain.common.model.NetworkError
import com.revolhope.domain.common.model.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

abstract class BaseRepositoryImpl {

    internal suspend inline fun <T> flowStateful(crossinline block: suspend () -> Flow<T>): Flow<State<T>> =
        block.invoke()
            .map { State.Success(it) as State<T> }
            .catch { e -> emit(catchToErrorState(e)) }
            .onStart { emit(State.Loading) }

    private fun <T> FlowCollector<T>.catchToErrorState(e: Throwable?): State<T> {
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
                message = e?.message
            }
        }
        return State.Error(
            message = message,
            messageRes = messageRes,
            errorCode = errorCode,
            throwable = e
        )
    }

    protected suspend inline fun <T> runStatefulFlow(
        emitLoadingState: Boolean = true,
        crossinline action: suspend () -> T
    ): Flow<State<T>> =
        flow {
            if(emitLoadingState) emit(State.Loading)
            val state = launchStateful(action)
            emit(state)
        }.flowOn(Dispatchers.Main)

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
