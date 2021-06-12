package com.revolhope.domain.feature.authentication.usecase

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.authentication.repository.UserRepository
import com.revolhope.domain.feature.authentication.request.LoginRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DoLoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(params: Params): Flow<State<Boolean>> {
        return userRepository.doLogin(params.request, params.isRememberMe).let { networkFlow ->
            networkFlow
        }
        /*var result: Flow<State<Boolean>>
        loginState.onCollectState(
            onSuccess = {

            },
            onError = {

            }
        ) {
            val userState = userRepository.fetchRemoteUser(
                params.request.email,
                params.request.pwd,
                params.isRememberMe
            )
            userState.onSuccess { user ->
                result = flowOf(
                    if (user != null) {
                        userRepository.insertLocalUser(userModel = user)
                        State.Success(true)
                    } else {
                        State.Error()
                    }
                )
            }
        }
        return if (loginState is State.Success) {
            val userState = userRepository.fetchRemoteUser(
                params.request.email,
                params.request.pwd,
                params.isRememberMe
            )
            if (userState is State.Success && userState.data != null) {
                userRepository.insertLocalUser(userModel = userState.data)
                State.Success(true)
            } else {
                State.Error()
            }
        } else {
            loginState
        }*/
    }

    data class Params(val request: LoginRequest, val isRememberMe: Boolean)
}
