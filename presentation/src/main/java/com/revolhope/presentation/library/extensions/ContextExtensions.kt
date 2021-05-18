package com.revolhope.presentation.library.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

// =================================================================================================
// Context
// =================================================================================================

// Properties --------------------------------------------------------------------------------------

inline val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

// Functions ---------------------------------------------------------------------------------------

fun Context.drawableOf(@DrawableRes drawableId: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableId)

fun Context.colorOf(@ColorRes colorId: Int): Int = ContextCompat.getColor(this, colorId)

fun Context.dimensionOf(@DimenRes dimId: Int): Float = resources.getDimension(dimId)
