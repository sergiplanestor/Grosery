package com.revolhope.domain.feature.authentication.usecase

import com.revolhope.domain.common.base.UseCase
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class FetchUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCase<FetchUserUseCase.RequestParams, UserModel?>() {

    override suspend fun build(
        scope: CoroutineScope,
        requestParams: RequestParams
    ): UseCaseParams<RequestParams, UserModel?> =
        UseCaseParams { userRepository.fetchLocalUser() }

    object RequestParams
}
