package com.revolhope.data.feature.user.datasource

import com.revolhope.data.feature.user.response.UserNetResponse


interface UserNetworkDataSource {

    suspend fun createUserWithEmailAndPassword(email: String, pwd: String): Boolean

    suspend fun signInWithEmailAndPassword(email: String, pwd: String): Boolean

    suspend fun fetchUserDataByEmail(email: String): UserNetResponse?

    suspend fun insertUser(userNetResponse: UserNetResponse): Boolean
}
