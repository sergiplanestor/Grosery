package com.revolhope.presentation.feature.login

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ActivityLoginBinding
import com.revolhope.presentation.feature.dashboard.DashboardActivity
import com.revolhope.presentation.feature.register.RegisterActivity
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.component.form.model.FormModel
import com.revolhope.presentation.library.component.form.view.FormSubmitButton
import com.revolhope.presentation.library.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private val isFormValid: Boolean
        get() = binding.emailFormInput.runValidators() &&
                binding.pwdFormInput.runValidators()

    private val user: UserModel? by lazy { intent?.extras?.getParcelable(EXTRA_USER) }

    companion object {

        private const val EXTRA_USER = "LOGIN_EXTRA_USER"

        fun start(baseActivity: BaseActivity, userModel: UserModel? = null) {
            baseActivity.startActivity(
                Intent(baseActivity, LoginActivity::class.java).apply {
                    putExtras(
                        bundleOf(
                            EXTRA_NAVIGATION_TRANSITION to NavTransition.LATERAL,
                            EXTRA_USER to userModel
                        )
                    )
                }
            )
        }
    }

    override fun inflateView(): View =
        ActivityLoginBinding.inflate(layoutInflater).let {
            binding = it
            it.root
        }

    override fun bindViews() {
        binding.emailFormInput.bind(
            FormModel.Email(
                hint = getString(R.string.email),
                value = user?.email
            )
        )
        binding.pwdFormInput.bind(FormModel.Password(hint = getString(R.string.password)))
        binding.rememberFormCheckbox.bind(
            FormModel.Checkbox(
                isChecked = false,
                hint = getString(R.string.remember_me),
                isRequired = false,
                isFieldValid = true
            )
        )
        binding.buttonRegister.setOnClickListener { RegisterActivity.start(this) }
        with(binding.formButtonSubmit) {
            text = getString(R.string.login)
            onSubmit = ::onSubmitForm
            onTimeoutReached = { onErrorReceived("T_FIXME: DEFAULT ERROR") }
        }
    }

    override fun initObservers() {
        observe(viewModel.loginResponseLiveData, ::onLoginResult)
    }

    private fun onSubmitForm() {
        if (isFormValid) {
            viewModel.doLogin(
                email = binding.emailFormInput.text ?: "",
                pwd = binding.pwdFormInput.text ?: "",
                isRememberMe = binding.rememberFormCheckbox.isChecked
            )
        } else {
            binding.formButtonSubmit.state = FormSubmitButton.State.IDLE
        }
    }

    private fun onLoginResult(isSuccess: Boolean) {
        binding.formButtonSubmit.state = FormSubmitButton.State.IDLE
        if (isSuccess) {
            DashboardActivity.start(this)
        } else {
            onErrorReceived("T_FIXME: DEFAULT ERROR")
        }
    }
}
