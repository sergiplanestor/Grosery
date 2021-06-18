package com.revolhope.domain.feature.authentication.usecase

import com.revolhope.domain.common.extensions.onSuccessThen
import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(params: Params): Flow<State<Boolean>> {
        return userRepository.registerUser(params.user).onSuccessThen(
            { userRepository.insertRemoteUser(params.user) },
            { userRepository.insertLocalUser(params.user) }
        )

        /*.map { registerState ->
            if (registerState is State.Success) {
                userRepository.insertRemoteUser(params.user)
                    .firstOrNull { it !is State.Loading }?.let { remoteState ->
                        if (remoteState is State.Success) {
                            userRepository.insertLocalUser(params.user).firstOrNull {
                                it !is State.Loading
                            } ?: State.Error("Error insert local user")
                        } else {
                            remoteState
                        }
                    } ?: State.Error("Error insert remote user")
            } else {
                registerState
            }
        }*/
    }

    data class Params(val user: UserModel)
}
