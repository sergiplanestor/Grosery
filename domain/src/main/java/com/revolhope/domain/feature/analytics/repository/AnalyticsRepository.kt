package com.revolhope.domain.feature.analytics.repository

import com.revolhope.domain.common.model.State
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    suspend fun reportCrash(
        throwable: Throwable,
        extraInfo: String? = null,
        customKeys: Map<String, Any> = emptyMap()
    ): Flow<State<Boolean>>
}