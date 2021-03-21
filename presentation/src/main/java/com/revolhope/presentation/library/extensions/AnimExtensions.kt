package com.revolhope.presentation.library.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.addListener

const val ALPHA_ANIM_DURATION = 300L

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

inline fun alphaAnimation(
    view: View,
    isShowing: Boolean,
    duration: Long = ALPHA_ANIM_DURATION,
    interpolator: TimeInterpolator = AccelerateInterpolator(),
    crossinline onStart: (animator: Animator?) -> Unit = {},
    crossinline onEnd: (animator: Animator?) -> Unit = {},
): ObjectAnimator = ObjectAnimator.ofFloat(
    view,
    "alpha",
    if (isShowing) 0f else 1f,
    if (isShowing) 1f else 0f
).apply {
    this.duration = duration
    this.interpolator = interpolator
    this.addListener(
        onStart = onStart,
        onEnd = onEnd
    )
}