package com.revolhope.presentation.feature.splash

import android.view.View
import androidx.core.view.isVisible
import com.revolhope.presentation.databinding.ActivitySplashBinding
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.extensions.alphaAnimation
import com.revolhope.presentation.library.extensions.animationListener

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    companion object {
        const val ALPHA_ANIM = 700L
        const val SPLASH_DURATION = 1000L
    }

    override fun inflateView(): View =
        ActivitySplashBinding.inflate(layoutInflater).let {
            binding = it
            it.root
        }

    override fun bindViews() {
        super.bindViews()

        alphaAnimation(
            view = binding.splashTextView,
            isShowing = true,
            duration = ALPHA_ANIM,
            onEnd = {
                binding.lottieAnimationView.addAnimatorListener(
                    animationListener(
                        onStart = {
                            alphaAnimation(
                                view = binding.lottieAnimationView,
                                isShowing = true
                            ).start()
                        },
                        onEnd = {
                            //DashboardActivity.start(baseActivity = this@SplashActivity)
                            //finish()
                        }
                    )
                )
                binding.lottieAnimationView.playAnimation()
            }
        ).start()
    }
}