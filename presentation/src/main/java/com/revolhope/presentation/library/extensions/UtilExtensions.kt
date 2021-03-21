package com.revolhope.presentation.library.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.core.content.ContextCompat

inline fun runOnUI(delay: Long = 0L, crossinline action: () -> Unit) =
    Handler(Looper.getMainLooper()).postDelayed(
        { action.invoke() },
        delay
    )

inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

inline val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

fun Context.drawable(drawableId: Int): Drawable? = ContextCompat.getDrawable(this, drawableId)

fun Context.color(colorId: Int): Int = ContextCompat.getColor(this, colorId)

fun Drawable?.applyTint(colorInt: Int) = this?.setTintList(ColorStateList.valueOf(colorInt))