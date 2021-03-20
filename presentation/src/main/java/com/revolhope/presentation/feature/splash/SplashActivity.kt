package com.revolhope.presentation.feature.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.revolhope.presentation.databinding.ActivitySplashBinding
import com.revolhope.presentation.library.base.BaseActivity
import androidx.core.animation.addListener

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    companion object {
        const val ALPHA_ANIMATION_DURATION = 500L
        const val SPLASH_DURATION = 1500L
    }

    override fun inflateView(): View =
        ActivitySplashBinding.inflate(layoutInflater).let {
            binding = it
            it.root
        }

    override fun bindViews() {
        super.bindViews()
        AnimatorSet().apply {
            playTogether(
                //ObjectAnimator.ofFloat(binding.splashTextView, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.lottieAnimationView, "alpha", 0f, 1f)
            )
            duration = ALPHA_ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addListener(
                onEnd = {
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            //DashboardActivity.start(baseActivity = this@SplashActivity)
                            //finish()
                        },
                        SPLASH_DURATION
                    )
                }
            )
            start()
        }
    }
}