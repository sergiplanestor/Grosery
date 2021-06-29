package com.revolhope.data.feature.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.revolhope.data.feature.analytics.agent.CrashlyticsSdkAgent
import com.revolhope.domain.common.extensions.takeIfNotNullOrBlank
import javax.inject.Inject


class CrashlyticsSdkAgentImpl @Inject constructor() : CrashlyticsSdkAgent {

    companion object {
        const val CRASHLYTICS_INIT_MESSAGE_LOG =
            "SDK initialized (This is a test)"
        const val CRASHLYTICS_CUSTOM_KEY_USERNAME = "Username"
        val initializationException =
            RuntimeException(
                "CrashlyticsAgentImpl: Initialization exception\n\n" +
                        "isAlreadyInit: false\n" +
                        "userId: null or username: null\n\n"  +
                        "Non fatal exceptiion, just warning (weird behavior)"
            )
    }

    private val sdk: FirebaseCrashlytics by lazy { FirebaseCrashlytics.getInstance() }
    private var isAlreadyInit: Boolean = false

    private suspend fun ensureInitialized(
        userId: String? = null,
        username: String? = null,
        notInitCustomCrash: Throwable = initializationException,
        after: suspend () -> Unit
    ) {
        when {
            !isAlreadyInit && !userId.isNullOrBlank() && !username.isNullOrBlank() -> {
                sdk.setUserId(userId)
                sdk.setCustomKey(CRASHLYTICS_CUSTOM_KEY_USERNAME, username)
                sdk.log(CRASHLYTICS_INIT_MESSAGE_LOG)
                isAlreadyInit = true
            }
            !isAlreadyInit -> sdk.recordException(notInitCustomCrash)
            else /* isAlreadyInit */ -> after.invoke()
        }
    }

    override suspend fun initialize(userId: String, username: String) {
        ensureInitialized(userId, username) { /* Nothing to do here */ }
    }

    override suspend fun reportCrash(
        throwable: Throwable,
        extraInfo: String?,
        customKeys: Map<String, Any>
    ) {
        ensureInitialized {
            extraInfo.takeIfNotNullOrBlank(sdk::log)
            customKeys.addAsCustomKeys(sdk)
            sdk.recordException(throwable)
        }
    }

    private fun Map<String, Any>.addAsCustomKeys(sdk: FirebaseCrashlytics) {
        entries.forEach { entry ->
            when (entry.value) {
                is Boolean -> sdk.setCustomKey(entry.key, entry.value as Boolean)
                is Int -> sdk.setCustomKey(entry.key, entry.value as Int)
                is Long -> sdk.setCustomKey(entry.key, entry.value as Long)
                is Double -> sdk.setCustomKey(entry.key, entry.value as Double)
                is Float -> sdk.setCustomKey(entry.key, entry.value as Float)
                is String -> sdk.setCustomKey(entry.key, entry.value as String)
            }
        }
    }
}