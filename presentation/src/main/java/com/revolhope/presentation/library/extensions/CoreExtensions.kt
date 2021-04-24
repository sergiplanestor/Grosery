package com.revolhope.presentation.library.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

inline fun runOnUI(delay: Long = 0L, crossinline action: () -> Unit) =
    Handler(Looper.getMainLooper()).postDelayed(
        { action.invoke() },
        delay
    )

inline fun <T> LifecycleOwner.observe(data: LiveData<T>, crossinline action: (T) -> Unit) =
    data.observe(this, { action.invoke(it) })

inline val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

fun Context.drawableFrom(@DrawableRes drawableId: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableId)

fun Context.colorFrom(@ColorRes colorId: Int): Int = ContextCompat.getColor(this, colorId)

fun Context.dimensionFrom(@DimenRes dimId: Int): Float = resources.getDimension(dimId)

fun Drawable?.applyTint(@ColorInt colorInt: Int) =
    this?.setTintList(ColorStateList.valueOf(colorInt))

inline fun <T> CoroutineScope.launchAsync(
    dispatcher: CoroutineContext = Dispatchers.IO,
    crossinline asyncTask: suspend () -> T,
    crossinline onCompleteTask: (T) -> Unit = {}
) = launch { withContext(dispatcher) { asyncTask.invoke() }.also(onCompleteTask) }
