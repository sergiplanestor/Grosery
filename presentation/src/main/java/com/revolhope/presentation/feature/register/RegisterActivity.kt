package com.revolhope.presentation.feature.register

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ActivityRegisterBinding
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.component.form.model.FormModel

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

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
        with(binding.formButtonSubmit) {
            text = "SUBMIT ME"
            onSubmit = {
                Toast.makeText(context, "On submit", Toast.LENGTH_LONG).show()
            }
            onTimeoutReached = {
                Toast.makeText(context, "On timeout", Toast.LENGTH_LONG).show()
            }
        }
    }
}