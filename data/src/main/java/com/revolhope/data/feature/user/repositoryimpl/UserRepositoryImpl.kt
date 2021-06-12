package com.revolhope.data.feature.user.repositoryimpl

import com.revolhope.data.common.BaseRepositoryImpl
import com.revolhope.data.feature.user.datasource.UserCacheDataSource
import com.revolhope.data.feature.user.datasource.UserLocalDataSource
import com.revolhope.data.feature.user.datasource.UserNetworkDataSource
import com.revolhope.data.feature.user.exception.UserNullPwdException
import com.revolhope.data.feature.user.mapper.UserMapper
import com.revolhope.data.feature.user.response.UserLocalResponse
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
        runStatefulFlow {
            UserCacheDataSource.user ?: localDataSource.fetchUser()
                ?.let(UserMapper::fromUserLocalResponseToModel)
        }

    override suspend fun fetchRemoteUser(
        email: String,
        pwd: String,
        isRememberMe: Boolean
    ): Flow<State<UserModel?>> =
        flowStateful {
            networkDataSource.fetchUserDataByEmail(email).mapNotNull {
                it?.let { UserMapper.fromUserNetResponseToModel(it, pwd, isRememberMe) }
            }
        }

    override suspend fun registerUser(userModel: UserModel): Flow<State<Boolean>> =
        flowStateful {
            if (userModel.pwd == null) throw UserNullPwdException()
            networkDataSource.createUserWithEmailAndPassword(
                email = userModel.email,
                pwd = userModel.pwd!!
            )
        }

    /*override suspend fun registerUser(userModel: UserModel): Flow<State<Boolean>> =
        runStatefulFlow {
            if (userModel.pwd == null) throw UserNullPwdException()
            networkDataSource.createUserWithEmailAndPassword(
                email = userModel.email,
                pwd = userModel.pwd!!
            )
        }*/

    override suspend fun insertLocalUser(userModel: UserModel): Flow<State<Boolean>> =
        runStatefulFlow {
            UserCacheDataSource.insert(userModel)
            localDataSource.insertOrUpdateUser(userModel.let(UserMapper::fromUserModelToLocalResponse))
            true
        }

    override suspend fun insertRemoteUser(userModel: UserModel): Flow<State<Boolean>> =
        flowStateful {
            networkDataSource.insertUser(userModel.let(UserMapper::fromUserModelToNetResponse))
        }

    override suspend fun doLogin(
        request: LoginRequest,
        isRememberMe: Boolean
    ): Flow<State<Boolean>> =
        flowStateful {
            networkDataSource.signInWithEmailAndPassword(
                email = request.email,
                pwd = request.pwd
            ).also { isSuccessFlow ->
                isSuccessFlow.firstOrNull()?.takeIf { it }?.let {
                    insertUserIfNeeded(request.email, request.pwd, isRememberMe)
                }
            }
        }
        /*runStatefulFlow {
            networkDataSource.signInWithEmailAndPassword(
                email = request.email,
                pwd = request.pwd
            ).also { isSuccess ->
                if (isSuccess) insertUserIfNeeded(request.email, request.pwd, isRememberMe)
            }
        }*/

    private suspend fun insertUserIfNeeded(email: String, pwd: String, isRememberMe: Boolean) {
        localDataSource.fetchUser().let { localUser ->
            if (localUser == null || localUser.email != email) {
                networkDataSource.fetchUserDataByEmail(email).firstOrNull { it != null }?.let { netUser ->
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
