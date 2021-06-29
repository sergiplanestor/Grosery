package com.revolhope.domain.common.base

import com.revolhope.domain.common.model.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

abstract class UseCase<REQUEST, RESPONSE> {

    open suspend fun invoke(scope: CoroutineScope, requestParams: REQUEST): Flow<State<RESPONSE>> =
        build(scope, requestParams).run {
            execute(
                scope = scope,
                replay = requestReplay,
                params = requestParams,
                request = request
            )
        }


    internal abstract suspend fun build(
        scope: CoroutineScope,
        requestParams: REQUEST
    ): UseCaseParams<REQUEST, RESPONSE>

    internal open suspend fun execute(
        scope: CoroutineScope,
        replay: Int = 1,
        params: REQUEST,
        request: suspend (REQUEST) -> Flow<State<RESPONSE>>
    ): Flow<State<RESPONSE>> =
        request.invoke(params).share(scope = scope, replay = replay)

    internal fun <T> Flow<State<T>>.share(
        scope: CoroutineScope,
        started: SharingStarted = SharingStarted.WhileSubscribed(),
        replay: Int
    ): Flow<State<T>> =
        shareIn(scope, started, replay)

    internal open class UseCaseParams<REQUEST, RESPONSE>(
        val requestReplay: Int = 1,
        val request: suspend (REQUEST) -> Flow<State<RESPONSE>>
    )
}

