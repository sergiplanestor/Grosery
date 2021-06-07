package com.revolhope.domain.feature.authentication.usecase

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<State<UserModel?>> = userRepository.fetchLocalUser()
}
