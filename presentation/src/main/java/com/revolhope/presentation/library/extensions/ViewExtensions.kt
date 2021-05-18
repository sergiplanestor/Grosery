package com.revolhope.presentation.library.extensions

import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_NONE
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.revolhope.domain.common.extensions.EMPTY_STRING
import com.revolhope.domain.common.extensions.SPACE_STRING

// =================================================================================================
// View
// =================================================================================================

// Properties --------------------------------------------------------------------------------------

inline var View.isVisibleAnimated: Boolean
    get() = visibility == View.VISIBLE && alpha != 0f
    set(value) {
        alphaAnimator(
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
        )
    }

// Functions ---------------------------------------------------------------------------------------

inline fun View.doOnGlobalLayout(crossinline block: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            block.invoke()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}

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

fun View.dimensionOf(@DimenRes dimId: Int): Float = resources.getDimension(dimId)

fun View.getString(@StringRes stringRes: Int): String = context.getString(stringRes)

fun View.getString(@StringRes stringRes: Int, vararg formatArgs: Any?): String =
    context.getString(stringRes, formatArgs)

// =================================================================================================
// TextView
// =================================================================================================

// Properties --------------------------------------------------------------------------------------

inline val TextView.textOrNull: String? get() = this.text?.toString()

inline val TextView.textOrEmpty: String get() = this.textOrNull.orEmpty()

// Functions ---------------------------------------------------------------------------------------

fun TextView.justify(enableJustify: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        justificationMode = if (enableJustify) {
            JUSTIFICATION_MODE_INTER_WORD
        } else {
            JUSTIFICATION_MODE_NONE
        }
    }
}

// =================================================================================================
// TextInputEditText
// =================================================================================================

// Properties --------------------------------------------------------------------------------------

inline val TextInputEditText.isEmpty: Boolean get() = this.text?.toString().isNullOrEmpty()

inline val TextInputEditText.textOrEmpty: String get() = this.text?.toString().orEmpty()

// =================================================================================================
// TextInputLayout
// =================================================================================================

// Properties --------------------------------------------------------------------------------------

inline val TextInputLayout.inputLayoutContainer: FrameLayout? get() =
    this.children.find { it is FrameLayout } as? FrameLayout

inline val TextInputLayout.errorLayoutContainer: LinearLayout? get() =
    this.children.find { it is LinearLayout } as? LinearLayout

inline val TextInputLayout.isValidUI: Boolean get() = this.error.isNullOrEmpty()

// Functions ---------------------------------------------------------------------------------------

fun TextInputLayout.invalidUI(error: String? = SPACE_STRING) { this.error = error ?: SPACE_STRING }

fun TextInputLayout.validUI() { this.error = null }
