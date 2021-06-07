package com.revolhope.data.feature.profile.datasource

import com.revolhope.data.feature.profile.response.ProfileResponse

interface ProfileNetworkDataSource {

    suspend fun fetchProfile(userId: String): ProfileResponse?

    suspend fun insertOrUpdateProfile(userId: String, profile: ProfileResponse): Boolean
}
