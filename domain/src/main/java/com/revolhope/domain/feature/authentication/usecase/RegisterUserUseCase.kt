package com.revolhope.domain.feature.authentication.usecase

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(params: Params): Flow<State<Boolean>> {
        return userRepository.registerUser(params.user).let {
            if (it.firstOrNull() is State.Success) {
                val remoteFlowState = userRepository.insertRemoteUser(params.user)
                if (remoteFlowState.firstOrNull() is State.Success) {
                    userRepository.insertLocalUser(params.user)
                } else {
                    remoteFlowState
                }
            } else {
                it
            }
        }

        // TODO: Remove when above code had been validated
        /*return if (registerState is State.Success) {
            val remoteState = userRepository.insertRemoteUser(params.user)
            if (remoteState is State.Success) {
                userRepository.insertLocalUser(params.user)
            }
            remoteState
        } else {
            registerState
        }*/
    }

    data class Params(val user: UserModel)
}
