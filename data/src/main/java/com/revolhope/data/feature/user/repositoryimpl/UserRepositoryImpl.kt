package com.revolhope.data.feature.user.repositoryimpl

import com.revolhope.data.common.BaseRepositoryImpl
import com.revolhope.data.feature.user.datasource.UserCacheDataSource
import com.revolhope.data.feature.user.datasource.UserLocalDataSource
import com.revolhope.data.feature.user.datasource.UserNetworkDataSource
import com.revolhope.data.feature.user.exception.UserNullPwdException
import com.revolhope.data.feature.user.mapper.UserMapper
import com.revolhope.data.feature.user.response.UserLocalResponse
import com.revolhope.domain.common.extensions.asFlow
import com.revolhope.domain.common.extensions.onFirstOrNullEmittedValue
import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.repository.UserRepository
import com.revolhope.domain.feature.authentication.request.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val networkDataSource: UserNetworkDataSource
) : UserRepository, BaseRepositoryImpl() {

    override suspend fun fetchLocalUser(): Flow<State<UserModel?>> =
        stateful {
            (UserCacheDataSource.user ?: localDataSource.fetchUser()
                ?.let(UserMapper::fromUserLocalResponseToModel)).asFlow()
        }

    override suspend fun fetchRemoteUser(
        email: String,
        pwd: String,
        isRememberMe: Boolean
    ): Flow<State<UserModel?>> =
        stateful {
            networkDataSource.fetchUserDataByEmail(email).mapNotNull {
                it?.let { UserMapper.fromUserNetResponseToModel(it, pwd, isRememberMe) }
            }
        }

    override suspend fun registerUser(userModel: UserModel): Flow<State<Boolean>> =
        stateful {
            if (userModel.pwd == null) throw UserNullPwdException()
            networkDataSource.createUserWithEmailAndPassword(
                email = userModel.email,
                pwd = userModel.pwd!!
            )
        }

    override suspend fun insertLocalUser(userModel: UserModel): Flow<State<Boolean>> =
        stateful {
            UserCacheDataSource.insert(userModel)
            localDataSource.insertOrUpdateUser(userModel.let(UserMapper::fromUserModelToLocalResponse))
            true.asFlow()
        }

    override suspend fun insertRemoteUser(userModel: UserModel): Flow<State<Boolean>> =
        stateful {
            networkDataSource.insertUser(userModel.let(UserMapper::fromUserModelToNetResponse))
        }

    override suspend fun doLogin(
        request: LoginRequest,
        isRememberMe: Boolean
    ): Flow<State<Boolean>> =
        stateful {
            networkDataSource.signInWithEmailAndPassword(
                email = request.email,
                pwd = request.pwd
            ).onFirstOrNullEmittedValue(
                block = {
                    insertUserIfNeeded(
                        request.email,
                        request.pwd,
                        isRememberMe
                    )
                }
            )
        }

    private suspend fun insertUserIfNeeded(email: String, pwd: String, isRememberMe: Boolean) {
        localDataSource.fetchUser().let { localUser ->
            if (localUser == null || localUser.email != email) {
                networkDataSource.fetchUserDataByEmail(email).firstOrNull { it != null }
                    ?.let { netUser ->
                        localDataSource.insertOrUpdateUser(
                            UserLocalResponse(
                                id = netUser.id,
                                name = netUser.name,
                                email = netUser.email,
                                pwd = pwd,
                                isRememberMe = if (isRememberMe) 1 else 0,
                                lastLogin = netUser.lastLogin
                            )
                        )
                    }
            }
        }
    }
}
