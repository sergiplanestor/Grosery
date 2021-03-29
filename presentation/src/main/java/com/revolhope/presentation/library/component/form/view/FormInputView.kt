package com.revolhope.presentation.library.component.form.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ComponentFormInputViewBinding
import com.revolhope.presentation.library.component.form.model.FormModel
import com.revolhope.presentation.library.extensions.drawableOf
import com.revolhope.presentation.library.extensions.inflater

class FormInputView<T : FormModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val EMPTY_ERROR = " "
    }

    private val binding = ComponentFormInputViewBinding.inflate(context.inflater, this, true)

    init { attrs?.let(::applyAttrs) }

    fun bind(model: T) {
        applyStyle(model)
        applyDrawable(model.startDrawable)
        binding.formInputLayout
        binding.formInputLayout.hint = model.hint
        binding.formEditText.inputType = model.inputType
        model.value?.let { binding.formEditText.setText(it) }
        model.helperText?.let { binding.formInputLayout.helperText = it }
        setupListeners(model)
    }

    private fun runValidators(model: T): Boolean {
        for (validator in model.validators) {
            model.isFieldValid = validator.matches(model.value)
            if (!model.isFieldValid) {
                onFieldInvalid(
                    validator.errorMessageResource?.let(context::getString) ?: EMPTY_ERROR
                )
                break
            }
        }
        if (model.isFieldValid) onFieldValid()
        return model.isFieldValid
    }

    private fun setupListeners(model: T) {
        binding.formEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !binding.formEditText.text.isNullOrBlank()) runValidators(model)
        }
        binding.formEditText.addTextChangedListener(
            afterTextChanged = { text ->
                binding.formInputLayout.error?.let {
                    if (text.isNullOrBlank()) {
                        binding.formInputLayout.error = null
                    } else {
                        runValidators(model)
                    }
                }
                model.value = text.toString()
            }
        )
    }

    private fun onFieldInvalid(message: String) {
        binding.formInputLayout.error = message
    }

    private fun onFieldValid() {
        binding.formInputLayout.error = null
    }

    private fun applyAttrs(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.FormInputView).apply {
            applyDrawable(getDrawable(R.styleable.FormInputView_startIcon))
        }.recycle()
    }

    private fun applyStyle(model: T) {
        when (model) {
            is FormModel.Text,
            is FormModel.Email -> {
                binding.formInputLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                binding.formInputLayout.endIconDrawable = context.drawableOf(R.drawable.ic_clear)
            }
            is FormModel.Password -> {
                binding.formInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                binding.formInputLayout.endIconDrawable = context.drawableOf(R.drawable.ic_password_selector)
            }
        }
    }



    private fun applyDrawable(drawable: Drawable?) {
        drawable?.let { binding.formInputLayout.startIconDrawable = it }
    }
}