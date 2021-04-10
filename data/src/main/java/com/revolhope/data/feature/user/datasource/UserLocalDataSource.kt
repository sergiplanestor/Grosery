package com.revolhope.data.feature.user.datasource

import com.revolhope.data.feature.user.response.UserLocalResponse

interface UserLocalDataSource {

    suspend fun fetchUser(): UserLocalResponse?

    suspend fun insertOrUpdateUser(user: UserLocalResponse)
}
