package com.revolhope.data.injection

import com.revolhope.data.feature.grocery.repositoryimpl.GroceryRepositoryImpl
import com.revolhope.data.feature.profile.repositoryimpl.ProfileRepositoryImpl
import com.revolhope.data.feature.user.repositoryimpl.UserRepositoryImpl
import com.revolhope.domain.feature.grocery.repository.GroceryRepository
import com.revolhope.domain.feature.authentication.repository.UserRepository
import com.revolhope.domain.feature.profile.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindGroceryRepository(
        groceryRepositoryImpl: GroceryRepositoryImpl
    ): GroceryRepository

    @Binds
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

}
