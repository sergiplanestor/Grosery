package com.revolhope.presentation.utils

import com.revolhope.domain.common.extensions.verbose
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.base.BaseFragment

enum class LifecycleEvent {
    ON_CREATE,
    ON_CREATE_VIEW,
    ON_VIEW_CREATED,
    ON_START,
    ON_RESUME,
    ON_PAUSE,
    ON_STOP,
    ON_DESTROY,
    ON_ATTACH,
    ON_DETACH;

    override fun toString(): String {
        return when (this) {
            ON_CREATE -> "has called OnCreate"
            ON_CREATE_VIEW -> "has called OnCreateView"
            ON_VIEW_CREATED -> "has called OnViewCreated"
            ON_START -> "has called OnStart"
            ON_RESUME -> "has called OnResume"
            ON_STOP -> "has called OnStop"
            ON_PAUSE -> "has called OnPause"
            ON_DESTROY -> "has called OnDestroy"
            ON_ATTACH -> "has called OnAttach"
            ON_DETACH -> "has called OnDetach"
        }
    }
}

const val LOG_LIFECYCLE_TAG = "--> Lifecycle"

inline fun <reified T : BaseFragment> T.logLifecycle(event: LifecycleEvent) {
    verbose(
        LOG_LIFECYCLE_TAG,
        message = "${this::class.java.simpleName} $event"
    )
}

inline fun <reified T : BaseActivity> T.logLifecycle(event: LifecycleEvent) {
    verbose(
        LOG_LIFECYCLE_TAG,
        message = "${this::class.java.simpleName} $event"
    )
}