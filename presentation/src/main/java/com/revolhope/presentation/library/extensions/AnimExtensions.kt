package com.revolhope.presentation.library.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.view.children

const val ROTATION_ANIM_DURATION = 200L
const val ALPHA_ANIM_DURATION = 300L
const val EXPAND_COLLAPSE_ANIM_DURATION = 500L

inline fun animationListener(
    crossinline onStart: (animator: Animator?) -> Unit = {},
    crossinline onEnd: (animator: Animator?) -> Unit = {},
    crossinline onCancel: (animator: Animator?) -> Unit = {},
    crossinline onRepeat: (animator: Animator?) -> Unit = {}
): Animator.AnimatorListener = object : Animator.AnimatorListener {
    override fun onAnimationStart(animator: Animator?) {
        onStart.invoke(animator)
    }

    override fun onAnimationEnd(animator: Animator?) {
        onEnd.invoke(animator)
    }

    override fun onAnimationCancel(animator: Animator?) {
        onCancel.invoke(animator)
    }

    override fun onAnimationRepeat(animator: Animator?) {
        onRepeat.invoke(animator)
    }
}

inline fun View.alphaAnimation(
    isShowing: Boolean,
    duration: Long = ALPHA_ANIM_DURATION,
    interpolator: TimeInterpolator = AccelerateInterpolator(),
    startImmediately: Boolean = true,
    crossinline onStart: (animator: Animator?) -> Unit = {},
    crossinline onEnd: (animator: Animator?) -> Unit = {}
): ObjectAnimator = ObjectAnimator.ofFloat(
    this,
    "alpha",
    if (isShowing) 0f else 1f,
    if (isShowing) 1f else 0f
).apply {
    this@alphaAnimation.visibility
    this.duration = duration
    this.interpolator = interpolator
    this.addListener(
        onStart = onStart,
        onEnd = onEnd
    )
    if (startImmediately) start()
}

inline fun View.expandCollapseAnimation(
    targetHeight: Int,
    duration: Long = EXPAND_COLLAPSE_ANIM_DURATION,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    isExpanding: Boolean,
    childrenIdsToAnimate: List<Int> = emptyList(),
    startImmediately: Boolean = true,
    crossinline onStart: () -> Unit = {},
    crossinline onEnd: () -> Unit = {}
): ValueAnimator = ValueAnimator.ofInt(height, targetHeight).apply {
    this.duration = duration
    this.interpolator = interpolator
    addUpdateListener {
        val updateValue = it.animatedValue as Int
        if (updateValue == height) onStart.invoke()
        if (updateValue == targetHeight) {
            if (childrenIdsToAnimate.isNotEmpty() && this is ViewGroup) {
                childrenIdsToAnimate.forEach { childId ->
                    children.find { child ->
                        child.id == childId
                    }?.alphaAnimation(isShowing = isExpanding)
                }
            }
            onEnd.invoke()
        }
        layoutParams = layoutParams.apply { height = updateValue }
    }
    if (startImmediately) start()
}

inline fun View.rotate(
    angle: Float = 180f,
    duration: Long = ROTATION_ANIM_DURATION,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    startImmediately: Boolean = true,
    crossinline onStart: () -> Unit = {},
    crossinline onEnd: () -> Unit = {}
): ViewPropertyAnimator = animate().apply {
    rotationBy(angle)
    this.duration = duration
    this.interpolator = interpolator
    withStartAction { onStart.invoke() }
    withEndAction { onEnd.invoke() }
}.also { if (startImmediately) it.start() }

