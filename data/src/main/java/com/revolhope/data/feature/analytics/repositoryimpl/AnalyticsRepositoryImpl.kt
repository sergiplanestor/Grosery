package com.revolhope.data.feature.analytics.repositoryimpl

import com.revolhope.data.common.BaseRepositoryImpl
import com.revolhope.data.feature.analytics.agent.CrashlyticsSdkAgent
import com.revolhope.domain.common.extensions.asFlow
import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.analytics.repository.AnalyticsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class AnalyticsRepositoryImpl @Inject constructor(
    private val agent: CrashlyticsSdkAgent
) : AnalyticsRepository, BaseRepositoryImpl() {

    override suspend fun reportCrash(
        throwable: Throwable,
        extraInfo: String?,
        customKeys: Map<String, Any>
    ): Flow<State<Boolean>> =
        stateful(isLoadingEnabled = false) {
            agent.reportCrash(throwable, extraInfo, customKeys)
            true.asFlow()
        }
}