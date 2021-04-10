package com.revolhope.data.feature.user.repositoryimpl

import com.revolhope.data.common.BaseRepositoryImpl
import com.revolhope.data.feature.user.datasource.UserNetworkDataSource
import com.revolhope.data.feature.user.datasource.UserLocalDataSource
import com.revolhope.data.feature.user.exception.UserNullPwdException
import com.revolhope.data.feature.user.mapper.UserMapper
import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.domain.feature.user.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val networkDataSource: UserNetworkDataSource
) : UserRepository, BaseRepositoryImpl() {

    override suspend fun fetchUser(): State<UserModel?> =
        launchStateful {
            localDataSource.fetchUser()?.let(UserMapper::fromUserResponseToModel)
        }

    override suspend fun insertUser(userModel: UserModel): State<Boolean> =
        launchStateful {
            if (userModel.pwd == null) throw UserNullPwdException()
            networkDataSource.createUserWithEmailAndPassword(
                email = userModel.email,
                pwd = userModel.pwd!!
            ).also { isSuccess ->
                if (isSuccess) {
                    localDataSource.insertOrUpdateUser(userModel.let(UserMapper::fromUserModelToResponse))
                }
            }
        }

    override suspend fun doLogin(userModel: UserModel): State<Boolean> =
        launchStateful {
            if (userModel.pwd == null) throw UserNullPwdException()
            networkDataSource.signInWithEmailAndPassword(userModel.email, userModel.pwd!!)
        }
}
