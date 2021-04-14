package com.revolhope.domain.feature.user.usecase

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.domain.feature.user.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(params: Params): State<Boolean> {
        val registerState = userRepository.registerUser(params.user)
        return if (registerState is State.Success) {
            val remoteState = userRepository.insertRemoteUser(params.user)
            if (remoteState is State.Success) {
                userRepository.insertLocalUser(params.user)
            }
            remoteState
        } else {
            registerState
        }
    }

    data class Params(val user: UserModel)
}
