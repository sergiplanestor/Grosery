package com.revolhope.domain.feature.analytics.usecase

import com.revolhope.domain.common.base.UseCase
import com.revolhope.domain.feature.analytics.repository.AnalyticsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

class ReportCrashUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) : UseCase<ReportCrashUseCase.RequestParams, Boolean>() {

    override suspend fun build(
        scope: CoroutineScope,
        requestParams: RequestParams
    ): UseCaseParams<RequestParams, Boolean> =
        UseCaseParams {
            analyticsRepository.reportCrash(
                throwable = requestParams.throwable,
                extraInfo = requestParams.extraInfo,
                customKeys = requestParams.customKeys
            )
        }


    data class RequestParams(
        val throwable: Throwable,
        val extraInfo: String? = null,
        val customKeys: Map<String, Any> = emptyMap()
    )
}