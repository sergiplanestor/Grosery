package com.revolhope.domain.feature.profile.repository

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.profile.model.ProfileModel
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun fetchProfile(userId: String): Flow<State<ProfileModel?>>

    suspend fun insertOrUpdateProfile(userId: String, profile: ProfileModel): Flow<State<Boolean>>

}
