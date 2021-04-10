package com.revolhope.presentation.feature.splash

import android.view.View
import androidx.activity.viewModels
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.presentation.databinding.ActivitySplashBinding
import com.revolhope.presentation.feature.dashboard.DashboardActivity
import com.revolhope.presentation.feature.login.LoginActivity
import com.revolhope.presentation.feature.register.RegisterActivity
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.extensions.alphaAnimation
import com.revolhope.presentation.library.extensions.animationListener
import com.revolhope.presentation.library.extensions.isVisibleAnimated
import com.revolhope.presentation.library.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    companion object {
        const val ALPHA_ANIM = 700L
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
                        onStart = { binding.lottieAnimationView.isVisibleAnimated = true },
                        onEnd = { viewModel.navigate() }
                    )
                )
                binding.lottieAnimationView.playAnimation()
            }
        ).start()
    }

    override fun initObservers() {
        super.initObservers()
        observe(viewModel.redirectToLoginLiveData, ::navigateToLogin)
        observe(viewModel.onLoginResponseLiveData, ::onLoginResponse)
    }

    private fun navigateToLogin(user: UserModel?) {
        LoginActivity.start(this, user)
        finish()
    }

    private fun onLoginResponse(isSuccess: Boolean) {
        if (isSuccess) {
            DashboardActivity.start(this)
            finish()
        } else {
            onErrorReceived("T_FIXME: DEFAULT ERROR")
        }
    }

}
