package com.revolhope.data.feature.profile.repositoryimpl

import com.revolhope.data.common.BaseRepositoryImpl
import com.revolhope.data.feature.profile.datasource.ProfileLocalDataSource
import com.revolhope.data.feature.profile.datasource.ProfileNetworkDataSource
import com.revolhope.data.feature.profile.mapper.ProfileMapper
import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.profile.model.ProfileModel
import com.revolhope.domain.feature.profile.repository.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val networkDataSource: ProfileNetworkDataSource,
    private val localDataSource: ProfileLocalDataSource
) : BaseRepositoryImpl(), ProfileRepository {

    override suspend fun fetchProfile(userId: String): State<ProfileModel?> =

        launchStateful {
            localDataSource.fetchProfile().let {
                // In case of being locally returns, if not, fetch from network and, in case of fetch
                // returns and store it locally
                it ?: networkDataSource.fetchProfile(userId)?.also { profile ->
                    localDataSource.insertOrUpdateProfile(profile)
                }
            }?.let(ProfileMapper::fromProfileResponseToModel)
        }

    override suspend fun insertOrUpdateProfile(userId: String, profile: ProfileModel): State<Boolean> =
        launchStateful {
            ProfileMapper.fromProfileModelToResponse(profile).run {
                val networkSuccess = networkDataSource.insertOrUpdateProfile(userId, this)
                if (networkSuccess) {
                    localDataSource.insertOrUpdateProfile(this)
                    true
                } else {
                    // TODO: Exception 1
                    throw RuntimeException("${this::class.java.simpleName} - find TODO: Exception 1")
                }
            }
        }
}
