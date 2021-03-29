package com.revolhope.data.feature.user.repositoryimpl

import com.revolhope.data.common.BaseRepositoryImpl
import com.revolhope.data.feature.storage.LocalDataSource
import com.revolhope.data.feature.user.mapper.UserMapper
import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.domain.feature.user.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : UserRepository, BaseRepositoryImpl() {

    override suspend fun fetchUser(): State<UserModel?> =
        launchStateful {
            localDataSource.fetchUser()?.let(UserMapper::fromUserResponseToModel)
        }
}