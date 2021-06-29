package com.revolhope.data.injection

import com.revolhope.data.feature.analytics.CrashlyticsSdkAgentImpl
import com.revolhope.data.feature.analytics.agent.CrashlyticsSdkAgent
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SdkAgentModule {

    @Binds
    abstract fun bindCrashlyticsSdkAgent(
        crashlyticsSdkAgentImpl: CrashlyticsSdkAgentImpl
    ): CrashlyticsSdkAgent
}