package com.revolhope.data.feature.user.datasource


interface UserNetworkDataSource {

    suspend fun createUserWithEmailAndPassword(email: String, pwd: String): Boolean

    suspend fun signInWithEmailAndPassword(email: String, pwd: String): Boolean
}
