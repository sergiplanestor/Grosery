package com.revolhope.data.injection

import com.revolhope.data.feature.grocery.datasource.GroceryNetworkDataSource
import com.revolhope.data.feature.profile.datasource.ProfileLocalDataSource
import com.revolhope.data.feature.profile.datasource.ProfileNetworkDataSource
import com.revolhope.data.feature.storage.local.LocalDataSourceImpl
import com.revolhope.data.feature.storage.network.FirebaseDataSourceImpl
import com.revolhope.data.feature.user.datasource.UserNetworkDataSource
import com.revolhope.data.feature.user.datasource.UserLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindUserLocalDataSource(
        localDataSourceImpl: LocalDataSourceImpl
    ): UserLocalDataSource

    @Binds
    abstract fun bindUserNetworkDataSource(
        firebaseDataSourceImpl: FirebaseDataSourceImpl
    ): UserNetworkDataSource

    @Binds
    abstract fun bindGroceryNetworkDataSource(
        firebaseDataSourceImpl: FirebaseDataSourceImpl
    ): GroceryNetworkDataSource

    @Binds
    abstract fun bindProfileNetworkDataSource(
        firebaseDataSourceImpl: FirebaseDataSourceImpl
    ): ProfileNetworkDataSource

    @Binds
    abstract fun bindProfileLocalDataSource(
        localDataSourceImpl: LocalDataSourceImpl
    ): ProfileLocalDataSource
}
