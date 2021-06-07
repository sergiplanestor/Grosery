package com.revolhope.domain.feature.authentication.repository

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.request.LoginRequest
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun fetchLocalUser(): Flow<State<UserModel?>>

    suspend fun fetchRemoteUser(
        email: String,
        pwd: String,
        isRememberMe: Boolean
    ): Flow<State<UserModel?>>

    suspend fun registerUser(userModel: UserModel): Flow<State<Boolean>>

    suspend fun insertLocalUser(userModel: UserModel): Flow<State<Boolean>>

    suspend fun insertRemoteUser(userModel: UserModel): Flow<State<Boolean>>

    suspend fun doLogin(request: LoginRequest, isRememberMe: Boolean): Flow<State<Boolean>>
}
