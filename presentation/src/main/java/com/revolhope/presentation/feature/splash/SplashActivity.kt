package com.revolhope.presentation.feature.splash

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.revolhope.domain.common.extensions.delay
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.presentation.databinding.ActivitySplashBinding
import com.revolhope.presentation.feature.dashboard.DashboardActivity
import com.revolhope.presentation.feature.login.LoginActivity
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.component.loader.LoadingMessageModel
import com.revolhope.presentation.library.extensions.alphaAnimator
import com.revolhope.presentation.library.extensions.animationListenerWith
import com.revolhope.presentation.library.extensions.isVisibleAnimated
import com.revolhope.presentation.library.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override val onLoadingLiveData: LiveData<Pair<Boolean, LoadingMessageModel?>>
        get() = viewModel.loadingLiveData
    override val onErrorLiveData: LiveData<String>
        get() = viewModel.errorLiveData

    companion object {
        const val ALPHA_ANIM = 700L
        const val NAVIGATION_DELAY = 500L
    }

    override fun inflateView(): View =
        ActivitySplashBinding.inflate(layoutInflater).let {
            binding = it
            it.root
        }

    override fun bindViews() {
        super.bindViews()
        binding.splashTextView.alphaAnimator(
            isShowing = true,
            duration = ALPHA_ANIM,
            onEnd = {
                binding.lottieAnimationView.addAnimatorListener(
                    animationListenerWith(
                        onStart = { binding.lottieAnimationView.isVisibleAnimated = true },
                        onEnd = { viewModel.navigate() }
                    )
                )
                binding.lottieAnimationView.playAnimation()
            }
        )
    }

    override fun initObservers() {
        super.initObservers()
        observe(viewModel.errorLiveData, ::onErrorReceived)
        observe(viewModel.errorResLiveData) { onErrorReceived(getString(it)) }
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
            onErrorReceived(/* default error */)
        }
    }

    override fun onErrorReceived(
        error: String?,
        onClick: (() -> Unit)?,
        onDismiss: (() -> Unit)?
    ) {
        super.onErrorReceived(error, onClick) {
            delay(NAVIGATION_DELAY) {
                navigateToLogin(viewModel.user)
            }
        }
    }
}
