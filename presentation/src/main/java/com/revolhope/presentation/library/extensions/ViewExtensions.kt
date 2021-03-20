package com.revolhope.presentation.library.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat

inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

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

inline val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

fun Context.drawable(drawableId: Int): Drawable? = ContextCompat.getDrawable(this, drawableId)

fun Context.color(colorId: Int): Int = ContextCompat.getColor(this, colorId)

fun Drawable?.applyTint(colorInt: Int) = this?.setTintList(ColorStateList.valueOf(colorInt))