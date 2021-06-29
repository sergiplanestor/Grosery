package com.revolhope.presentation.library.extensions

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.ColorInt
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

inline fun <T> LifecycleOwner.observe(data: LiveData<T>, crossinline action: (T) -> Unit) =
    data.observe(this, { action.invoke(it) })

fun Drawable?.applyTint(@ColorInt colorInt: Int) =
    this?.setTintList(ColorStateList.valueOf(colorInt))