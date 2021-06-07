package com.revolhope.presentation.library.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_NONE
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.core.widget.TextViewCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.revolhope.domain.common.extensions.SPACE_STRING
import com.revolhope.domain.feature.profile.model.ProfileAvatar
import com.revolhope.presentation.R

// =================================================================================================
// View
// =================================================================================================

// Properties --------------------------------------------------------------------------------------

inline var View.isVisibleAnimated: Boolean
    get() = visibility == View.VISIBLE && alpha != 0f
    set(value) {
        val isNoAnimationNeeded =
            // value = true and view is already visible
            (value && visibility == View.VISIBLE && alpha == 1f) ||
                    // value = false and view is already invisible or gone
                    (!value && visibility != View.VISIBLE)

        if (isNoAnimationNeeded.not()) {
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
    context.getString(stringRes, *formatArgs)

// =================================================================================================
// TextView
// =================================================================================================

// Model -------------------------------------------------------------------------------------------

enum class DrawablePosition {
    START,
    END,
    TOP,
    BOTTOM,
    TEXT_START,
    TEXT_END,
    TEXT_TOP,

}

data class TextDrawableUiModel(
    val drawable: Drawable? = null,
    @DrawableRes val drawableRes: Int? = null,
    @Dimension val sizeDps: Int? = 30.dp,
    @Dimension val paddingDps: Int? = 8.dp,
    @ColorInt val tintColorInt: Int? = null,
    val position: DrawablePosition = DrawablePosition.START
) {
    fun getDrawable(context: Context): Drawable? =
        drawable ?: drawableRes?.let(context::drawableOf)
}

data class ClickableViewUiModel(
    val text: CharSequence,
    val drawableUiModel: TextDrawableUiModel,
    val onClick: () -> Unit
)

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

fun TextView.drawable(model: TextDrawableUiModel) {
    model.getDrawable(context)?.let { drawable ->
        when (model.position) {
            DrawablePosition.START,
            DrawablePosition.TEXT_START -> this.setCompoundDrawables(
                drawable, null, null, null
            )
            DrawablePosition.END,
            DrawablePosition.TEXT_END -> this.setCompoundDrawables(
                null, null, drawable, null
            )
            DrawablePosition.TOP,
            DrawablePosition.TEXT_TOP -> this.setCompoundDrawables(
                null, drawable, null, null
            )
            DrawablePosition.BOTTOM -> this.setCompoundDrawables(
                null, null, null, drawable
            )
        }
        model.tintColorInt?.let {
            TextViewCompat.setCompoundDrawableTintList(
                this,
                ColorStateList.valueOf(it)
            )
        }
        model.paddingDps?.let { compoundDrawablePadding = it }
    }
}

fun TextView.bindClickableModel(model: ClickableViewUiModel) {
    text = model.text
    drawable(model.drawableUiModel)
    setOnClickListener { model.onClick.invoke() }
}

// =================================================================================================
// Button && Material Button
// =================================================================================================

// Functions ---------------------------------------------------------------------------------------

fun MaterialButton.bindClickableModel(model: ClickableViewUiModel) {
    text = model.text
    drawable(model.drawableUiModel)
    setOnClickListener { model.onClick.invoke() }
}

fun MaterialButton.drawable(model: TextDrawableUiModel) {
    model.getDrawable(context)?.let(::setIcon).also {
        when (model.position) {
            DrawablePosition.START -> MaterialButton.ICON_GRAVITY_START
            DrawablePosition.END -> MaterialButton.ICON_GRAVITY_END
            DrawablePosition.TOP -> MaterialButton.ICON_GRAVITY_TOP
            DrawablePosition.TEXT_START -> MaterialButton.ICON_GRAVITY_TEXT_START
            DrawablePosition.TEXT_END -> MaterialButton.ICON_GRAVITY_TEXT_END
            DrawablePosition.TEXT_TOP -> MaterialButton.ICON_GRAVITY_TEXT_TOP
            else -> null
        }?.let(::setIconGravity)
        model.tintColorInt?.let { iconTint = ColorStateList.valueOf(it) }
        model.paddingDps?.let(::setIconPadding)
        model.sizeDps?.let(::setIconSize)
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

inline val TextInputLayout.inputLayoutContainer: FrameLayout?
    get() =
        this.children.find { it is FrameLayout } as? FrameLayout

inline val TextInputLayout.errorLayoutContainer: LinearLayout?
    get() =
        this.children.find { it is LinearLayout } as? LinearLayout

inline val TextInputLayout.isValidUI: Boolean get() = this.error.isNullOrEmpty()

// Functions ---------------------------------------------------------------------------------------

fun TextInputLayout.invalidUI(error: String? = SPACE_STRING) {
    this.error = error ?: SPACE_STRING
}

fun TextInputLayout.validUI() {
    this.error = null
}

// =================================================================================================
// ImageView
// =================================================================================================

// Properties --------------------------------------------------------------------------------------

inline var ImageView.avatar: ProfileAvatar?
    get() = ProfileAvatar.fromId(tag as? Int).takeUnless { it == ProfileAvatar.NONE }
    set(value) {
        value.takeUnless { it == ProfileAvatar.NONE }?.let { avatar ->
            tag = avatar.id
            setImageResource(
                when (avatar) {
                    ProfileAvatar.CAT -> R.drawable.img_avatar_cat
                    ProfileAvatar.LION -> R.drawable.img_avatar_lion
                    ProfileAvatar.OSTRICH -> R.drawable.img_avatar_ostrich
                    ProfileAvatar.OWL -> R.drawable.img_avatar_owl
                    ProfileAvatar.ZEBRA -> R.drawable.img_avatar_zebra
                    ProfileAvatar.UNICORN -> R.drawable.img_avatar_unicorn
                    /* This case is not possible */
                    ProfileAvatar.NONE ->
                        throw RuntimeException("file: ViewExtensions.kt, cause: Trying to load NONE avatar into imageview")
                }
            )
        }
    }
