package com.revolhope.data.feature.profile.repositoryimpl

import com.revolhope.data.common.BaseRepositoryImpl
import com.revolhope.data.feature.profile.datasource.ProfileLocalDataSource
import com.revolhope.data.feature.profile.datasource.ProfileNetworkDataSource
import com.revolhope.data.feature.profile.mapper.ProfileMapper
import com.revolhope.domain.common.extensions.asFlow
import com.revolhope.domain.common.extensions.mapIfNotNull
import com.revolhope.domain.common.extensions.onFirstOrNullEmittedValue
import com.revolhope.domain.common.extensions.onFirstSuccessEmitted
import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.profile.model.ProfileModel
import com.revolhope.domain.feature.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val networkDataSource: ProfileNetworkDataSource,
    private val localDataSource: ProfileLocalDataSource
) : BaseRepositoryImpl(), ProfileRepository {

    override suspend fun fetchProfile(userId: String): Flow<State<ProfileModel?>> =
        stateful {
            localDataSource.fetchProfile().let {
                // In case of being locally returns, if not, fetch from network and, in case of fetch
                // returns and store it locally
                it?.asFlow() ?: networkDataSource.fetchProfile(userId).onFirstOrNullEmittedValue(
                    predicate = { this != null },
                    block = {
                        // At this point 'this' must be always non-null
                        this?.let { localDataSource.insertOrUpdateProfile(this) }
                    }
                )
            }.mapIfNotNull(ProfileMapper::fromProfileResponseToModel)
        }

    override suspend fun insertOrUpdateProfile(
        userId: String,
        profile: ProfileModel
    ): Flow<State<Boolean>> =
        stateful {
            ProfileMapper.fromProfileModelToResponse(profile).run {
                networkDataSource.insertOrUpdateProfile(userId, this).onFirstSuccessEmitted {
                    localDataSource.insertOrUpdateProfile(this)
                }
            }
        }
}
