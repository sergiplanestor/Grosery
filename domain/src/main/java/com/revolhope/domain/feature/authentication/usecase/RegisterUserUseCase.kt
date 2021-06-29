package com.revolhope.domain.feature.authentication.usecase

import android.util.Log
import com.revolhope.domain.common.base.UseCase
import com.revolhope.domain.common.model.State
import com.revolhope.domain.common.model.dataAsBooleanStateOrFalse
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCase<RegisterUserUseCase.RequestParams, Boolean>() {

    override suspend fun build(
        scope: CoroutineScope,
        requestParams: RequestParams
    ): UseCaseParams<RequestParams, Boolean> =
        UseCaseParams { userRepository.registerUser(it.user) }

    // TODO: Remove LOGS

    override suspend fun execute(
        scope: CoroutineScope,
        replay: Int,
        params: RequestParams,
        request: suspend (RequestParams) -> Flow<State<Boolean>>
    ): Flow<State<Boolean>> =
        request.invoke(params)
            .map { registerState ->
                if (registerState.dataAsBooleanStateOrFalse()) {
                    userRepository.insertRemoteUser(params.user)
                        .firstOrNull {
                            it !is State.Loading
                        }?.let { remoteState ->
                            if (remoteState.dataAsBooleanStateOrFalse()) {
                                userRepository.insertLocalUser(params.user)
                                    .firstOrNull {
                                        it !is State.Loading
                                    }?.also { Log.v("TEEEST", "insertLocalUser Worked -> $it") }
                            } else {
                                remoteState
                            }
                        }?.also { Log.v("TEEEST", "insertRemoteUser Worked -> $it") }
                        ?: throw RuntimeException("RuntimeException - RegisterUseCase")
                } else {
                    registerState
                }.also {
                    Log.v("TEEEST", "mapping: $registerState")
                }
            }

    data class RequestParams(val user: UserModel)
}
