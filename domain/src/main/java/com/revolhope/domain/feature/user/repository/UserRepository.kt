package com.revolhope.domain.feature.user.repository

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.user.model.UserModel

interface UserRepository {

    suspend fun fetchUser(): State<UserModel?>

}