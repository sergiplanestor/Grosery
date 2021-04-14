package com.revolhope.domain.feature.user.repository

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.domain.feature.user.request.LoginRequest

interface UserRepository {

    suspend fun fetchLocalUser(): State<UserModel?>

    suspend fun fetchRemoteUser(
        email: String,
        pwd: String,
        isRememberMe: Boolean
    ): State<UserModel?>

    suspend fun registerUser(userModel: UserModel): State<Boolean>

    suspend fun insertLocalUser(userModel: UserModel): State<Boolean>

    suspend fun insertRemoteUser(userModel: UserModel): State<Boolean>

    suspend fun doLogin(request: LoginRequest, isRememberMe: Boolean): State<Boolean>
}
