package com.revolhope.data.feature.profile.datasource

import com.revolhope.data.feature.profile.response.ProfileResponse
import kotlinx.coroutines.flow.Flow

interface ProfileNetworkDataSource {

    suspend fun fetchProfile(userId: String): Flow<ProfileResponse?>

    suspend fun insertOrUpdateProfile(userId: String, profile: ProfileResponse): Flow<Boolean>
}
