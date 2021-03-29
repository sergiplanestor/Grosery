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



inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

inline val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

fun Context.drawableOf(drawableId: Int): Drawable? = ContextCompat.getDrawable(this, drawableId)

fun Context.colorOf(colorId: Int): Int = ContextCompat.getColor(this, colorId)

fun Drawable?.applyTint(colorInt: Int) = this?.setTintList(ColorStateList.valueOf(colorInt))

inline fun <T> CoroutineScope.launchAsync(
    dispatcher: CoroutineContext = Dispatchers.IO,
    crossinline asyncTask: suspend () -> T,
    crossinline onCompleteTask: (T) -> Unit = {}
) = launch { withContext(dispatcher) { asyncTask.invoke() }.also(onCompleteTask) }