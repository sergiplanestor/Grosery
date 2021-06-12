package com.revolhope.data.feature.user.datasource

import com.revolhope.data.feature.user.response.UserNetResponse
import kotlinx.coroutines.flow.Flow

interface UserNetworkDataSource {

    suspend fun createUserWithEmailAndPassword(email: String, pwd: String): Flow<Boolean>

    suspend fun signInWithEmailAndPassword(email: String, pwd: String): Flow<Boolean>

    suspend fun fetchUserDataByEmail(email: String): Flow<UserNetResponse?>

    suspend fun insertUser(userNetResponse: UserNetResponse): Flow<Boolean>
}
