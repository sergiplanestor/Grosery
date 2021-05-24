package com.revolhope.data.feature.user.repositoryimpl

import com.revolhope.data.common.BaseRepositoryImpl
import com.revolhope.data.feature.user.datasource.UserCacheDataSource
import com.revolhope.data.feature.user.datasource.UserNetworkDataSource
import com.revolhope.data.feature.user.datasource.UserLocalDataSource
import com.revolhope.data.feature.user.exception.UserNullPwdException
import com.revolhope.data.feature.user.mapper.UserMapper
import com.revolhope.data.feature.user.response.UserLocalResponse
import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.repository.UserRepository
import com.revolhope.domain.feature.authentication.request.LoginRequest
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val networkDataSource: UserNetworkDataSource
) : UserRepository, BaseRepositoryImpl() {

    override suspend fun fetchLocalUser(): State<UserModel?> =
        launchStateful {
            UserCacheDataSource.user ?: localDataSource.fetchUser()
                ?.let(UserMapper::fromUserLocalResponseToModel)
        }

    override suspend fun fetchRemoteUser(
        email: String,
        pwd: String,
        isRememberMe: Boolean
    ): State<UserModel?> =
        launchStateful {
            networkDataSource.fetchUserDataByEmail(email)
                ?.let { UserMapper.fromUserNetResponseToModel(it, pwd, isRememberMe) }
        }

    override suspend fun registerUser(userModel: UserModel): State<Boolean> =
        launchStateful {
            if (userModel.pwd == null) throw UserNullPwdException()
            networkDataSource.createUserWithEmailAndPassword(
                email = userModel.email,
                pwd = userModel.pwd!!
            )
        }

    override suspend fun insertLocalUser(userModel: UserModel): State<Boolean> =
        launchStateful {
            UserCacheDataSource.insert(userModel)
            localDataSource.insertOrUpdateUser(userModel.let(UserMapper::fromUserModelToLocalResponse))
            true
        }

    override suspend fun insertRemoteUser(userModel: UserModel): State<Boolean> =
        launchStateful {
            networkDataSource.insertUser(userModel.let(UserMapper::fromUserModelToNetResponse))
        }

    override suspend fun doLogin(request: LoginRequest, isRememberMe: Boolean): State<Boolean> =
        launchStateful {
            networkDataSource.signInWithEmailAndPassword(
                email = request.email,
                pwd = request.pwd
            ).also { isSuccess ->
                if (isSuccess) insertUserIfNeeded(request.email, request.pwd, isRememberMe)
            }
        }

    private suspend fun insertUserIfNeeded(email: String, pwd: String, isRememberMe: Boolean) {
        localDataSource.fetchUser().let { localUser ->
            if (localUser == null || localUser.email != email) {
                networkDataSource.fetchUserDataByEmail(email)?.let { netUser ->
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
