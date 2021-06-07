package com.revolhope.data.feature.profile.datasource

import com.revolhope.data.feature.profile.response.ProfileResponse

interface ProfileLocalDataSource {

    suspend fun fetchProfile(): ProfileResponse?

    suspend fun insertOrUpdateProfile(profile: ProfileResponse)

}
