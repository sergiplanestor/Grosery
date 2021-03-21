package com.revolhope.presentation.library.extensions

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout

fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null
    do {
        when (view) {
            is CoordinatorLayout -> return view
            is FrameLayout -> {
                if (view.id == android.R.id.content) return view else fallback = view
            }
        }

        view = if (view?.parent is View) view.parent as View else null

    } while (view != null)

    return fallback
}

inline var View.isVisibleAnimated: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        alphaAnimation(
            view = this,
            isShowing = value,
            onStart = {
                if (value && visibility != View.VISIBLE) {
                    alpha = 0f
                    visibility = View.VISIBLE
                }
            },
            onEnd = {
                if (!value) {
                    visibility = View.GONE
                    alpha = 1f
                }
            }
        ).start()
    }