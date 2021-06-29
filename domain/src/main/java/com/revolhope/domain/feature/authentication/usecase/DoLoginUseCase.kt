package com.revolhope.domain.feature.authentication.usecase

import com.revolhope.domain.common.base.UseCase
import com.revolhope.domain.common.extensions.firstNonLoading
import com.revolhope.domain.common.model.State
import com.revolhope.domain.common.model.dataAsBooleanStateOrFalse
import com.revolhope.domain.common.model.isSuccessAndDataNonNull
import com.revolhope.domain.common.model.isSuccessAndDataNull
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.repository.UserRepository
import com.revolhope.domain.feature.authentication.request.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DoLoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCase<DoLoginUseCase.RequestParams, Boolean>() {

    override suspend fun build(
        scope: CoroutineScope,
        requestParams: RequestParams
    ): UseCaseParams<RequestParams, Boolean> =
        UseCaseParams {
            userRepository.doLogin(
                requestParams.request,
                requestParams.isRememberMe
            )
        }

    override suspend fun execute(
        scope: CoroutineScope,
        replay: Int,
        params: RequestParams,
        request: suspend (RequestParams) -> Flow<State<Boolean>>
    ): Flow<State<Boolean>> =
        request.invoke(params)
            .map { loginState ->
                if (loginState.dataAsBooleanStateOrFalse()) {
                    val remoteFetchState = userRepository.fetchRemoteUser(
                        params.request.email,
                        params.request.pwd,
                        params.isRememberMe
                    ).firstNonLoading()
                    when {
                        remoteFetchState != null && remoteFetchState.isSuccessAndDataNonNull -> {
                            userRepository.insertLocalUser(
                                userModel = (remoteFetchState as State.Success<UserModel?>).data!!
                            ).firstNonLoading()
                                ?: State.Error(/* TODO: Exception to inform that user couldn't be stored locally */)
                        }
                        remoteFetchState != null && remoteFetchState.isSuccessAndDataNull -> {
                            State.Error(/* TODO: Exception to inform that user couldn't be stored locally */)
                        }
                        else /* remoteFetchState == null */ -> {
                            State.Error(/* TODO: Exception to inform that user couldn't be fetched from remote */)
                        }
                    }
                } else {
                    loginState
                }
            }

    data class RequestParams(
        val request: LoginRequest,
        val isRememberMe: Boolean
    )
}
