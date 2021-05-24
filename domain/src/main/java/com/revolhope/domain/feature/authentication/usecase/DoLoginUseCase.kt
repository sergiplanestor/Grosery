package com.revolhope.domain.feature.authentication.usecase

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.authentication.repository.UserRepository
import com.revolhope.domain.feature.authentication.request.LoginRequest
import javax.inject.Inject

class DoLoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(params: Params): State<Boolean> {
        val loginState = userRepository.doLogin(params.request, params.isRememberMe)
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
        }
    }

    data class Params(val request: LoginRequest, val isRememberMe: Boolean)
}
