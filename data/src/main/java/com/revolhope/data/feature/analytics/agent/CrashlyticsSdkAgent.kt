package com.revolhope.data.feature.analytics.agent

interface CrashlyticsSdkAgent {

    suspend fun initialize(userId: String, username: String)

    suspend fun reportCrash(
        throwable: Throwable,
        extraInfo: String? = null,
        customKeys: Map<String, Any> = emptyMap()
    )
}