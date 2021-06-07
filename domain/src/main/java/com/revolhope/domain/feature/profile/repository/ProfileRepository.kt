package com.revolhope.domain.feature.profile.repository

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.profile.model.ProfileModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {

    suspend fun fetchProfile(userId: String): State<ProfileModel?>

    suspend fun insertOrUpdateProfile(userId: String, profile: ProfileModel): State<Boolean>

}
