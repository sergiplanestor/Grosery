package com.revolhope.presentation.library.component.form.view

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ComponentFormInputViewBinding
import com.revolhope.presentation.library.component.form.model.FormModel
import com.revolhope.presentation.library.extensions.applyTint
import com.revolhope.presentation.library.extensions.colorFrom
import com.revolhope.presentation.library.extensions.drawableFrom
import com.revolhope.presentation.library.extensions.inflater

class FormInputView<T : FormModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FormView<T>(context, attrs, defStyleAttr) {

    companion object {
        private const val EMPTY_ERROR = " "
    }

    private val binding = ComponentFormInputViewBinding.inflate(context.inflater, this, true)

    val text: String? get() = model?.value

    init {
        attrs?.let(::applyAttrs)
    }

    override fun bind(model: T) {
        super.bind(model)
        applyStyle()
        applyDrawable(model.startDrawable)
        binding.formInputLayout.hint = model.hint
        binding.formEditText.inputType = model.inputType
        model.value?.let { binding.formEditText.setText(it) }
        model.helperText?.let { binding.formInputLayout.helperText = it }
        setupListeners()
    }

    private fun setupListeners() {
        binding.formEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !binding.formEditText.text.isNullOrBlank()) runValidators()
        }
        binding.formEditText.addTextChangedListener(
            afterTextChanged = { text ->
                binding.formInputLayout.error?.let {
                    if (text.isNullOrBlank()) {
                        binding.formInputLayout.error = null
                    } else {
                        runValidators()
                    }
                }
                model?.value = text.toString()
            }
        )
    }

    override fun onFieldInvalid(message: String) {
        binding.formInputLayout.error = message
    }

    override fun onFieldValid() {
        binding.formInputLayout.error = null
    }

    private fun applyAttrs(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.FormInputView).apply {
            applyDrawable(getDrawable(R.styleable.FormInputView_startIcon))
        }.recycle()
    }

    private fun applyStyle() {
        when (model) {
            is FormModel.Text,
            is FormModel.Email -> {
                binding.formInputLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                binding.formInputLayout.endIconDrawable = context.drawableFrom(R.drawable.ic_clear)
            }
            is FormModel.Password -> {
                binding.formInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                binding.formInputLayout.endIconDrawable =
                    context.drawableFrom(R.drawable.ic_password_selector).apply {
                        val color = context.colorFrom(R.color.gray_800)
                        (this as? StateListDrawable)?.mutate().applyTint(color)
                    }
                binding.formInputLayout.isEndIconCheckable = true
            }
        }
    }

    private fun applyDrawable(drawable: Drawable?) {
        drawable?.let { binding.formInputLayout.startIconDrawable = it }
    }
}
