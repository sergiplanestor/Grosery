package com.revolhope.presentation.feature.register

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ActivityRegisterBinding
import com.revolhope.presentation.feature.dashboard.DashboardActivity
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.component.form.model.FormModel
import com.revolhope.presentation.library.component.form.view.FormSubmitButton
import com.revolhope.presentation.library.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    private val isFormValid: Boolean
        get() = binding.usernameFormInput.runValidators() &&
                binding.emailFormInput.runValidators() &&
                binding.pwdFormInput.runValidators()

    companion object {
        fun start(baseActivity: BaseActivity) {
            baseActivity.startActivity(
                Intent(baseActivity, RegisterActivity::class.java).apply {
                    putExtras(
                        bundleOf(
                            EXTRA_NAVIGATION_TRANSITION to NavTransition.LATERAL
                        )
                    )
                }
            )
        }
    }

    override fun inflateView(): View =
        ActivityRegisterBinding.inflate(layoutInflater).let {
            binding = it
            it.root
        }

    override fun bindViews() {
        binding.usernameFormInput.bind(
            FormModel.Text(
                hint = getString(R.string.username),
                helperText = getString(R.string.helper_optional),
                isRequired = false,
                isFieldValid = true
            )
        )
        binding.emailFormInput.bind(FormModel.Email(hint = getString(R.string.email)))
        binding.pwdFormInput.bind(FormModel.Password(hint = getString(R.string.password)))
        binding.rememberFormCheckbox.bind(
            FormModel.Checkbox(
                isChecked = true,
                hint = getString(R.string.remember_me),
                isRequired = false,
                isFieldValid = true
            )
        )
        with(binding.formButtonSubmit) {
            text = getString(R.string.register)
            onSubmit = ::onSubmitForm
            onTimeoutReached = { onErrorReceived("T_FIXME: DEFAULT ERROR") }
        }
        binding.buttonLogin.setOnClickListener { onBackPressed() }
    }

    override fun initObservers() {
        observe(viewModel.onRegisterResultLiveData, ::onRegisterResult)
    }

    private fun onSubmitForm() {
        if (isFormValid) {
            viewModel.doRegister(
                username = binding.usernameFormInput.text,
                email = binding.emailFormInput.text ?: "",
                pwd = binding.pwdFormInput.text ?: "",
                isRememberMe = binding.rememberFormCheckbox.isChecked
            )
        } else {
            binding.formButtonSubmit.state = FormSubmitButton.State.IDLE
        }
    }

    private fun onRegisterResult(isSuccess: Boolean) {
        binding.formButtonSubmit.state = FormSubmitButton.State.IDLE
        if (isSuccess) {
            DashboardActivity.start(this)
        } else {
            onErrorReceived("T_FIXME: DEFAULT ERROR")
        }
    }
}
