package com.revolhope.domain.feature.user.usecase

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.domain.feature.user.repository.UserRepository
import javax.inject.Inject

class InsertUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(params: Params): State<Boolean> =
        userRepository.insertUser(params.user)

    data class Params(val user: UserModel)
}
