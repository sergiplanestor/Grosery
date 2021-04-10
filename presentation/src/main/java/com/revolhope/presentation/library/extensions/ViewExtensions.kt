package com.revolhope.presentation.library.extensions

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_NONE
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ImageSpan
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import java.util.concurrent.atomic.AtomicBoolean


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
                if (value && (visibility != View.VISIBLE || alpha == 0f)) {
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

fun TextView.justify(enableJustify: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        justificationMode = if (enableJustify) {
            JUSTIFICATION_MODE_INTER_WORD
        } else {
            JUSTIFICATION_MODE_NONE
        }
    }
}
