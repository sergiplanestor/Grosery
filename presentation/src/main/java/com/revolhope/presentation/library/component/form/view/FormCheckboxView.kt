package com.revolhope.presentation.library.component.form.view

import android.content.Context
import android.util.AttributeSet
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ComponentFormCheckboxViewBinding
import com.revolhope.presentation.library.component.form.model.FormModel
import com.revolhope.presentation.library.extensions.colorFrom
import com.revolhope.presentation.library.extensions.inflater
import com.revolhope.presentation.library.extensions.justify

class FormCheckboxView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FormView<FormModel.Checkbox>(context, attrs, defStyleAttr) {

    private val binding = ComponentFormCheckboxViewBinding.inflate(context.inflater, this, true)

    val isChecked: Boolean get() = model?.isChecked ?: false

    var text: String = ""
        set(value) {
            field = value
            binding.formCheckboxText.text = value
        }

    override fun bind(model: FormModel.Checkbox) {
        super.bind(model)
        binding.formCheckboxText.text = model.hint
        binding.formCheckbox.isChecked = model.isChecked
        binding.formCheckboxText.justify(enableJustify = model.isTextJustified)
        setupListeners()
    }

    override fun onFieldValid() {
        binding.formCheckboxText.setTextColor(context.colorFrom(R.color.primaryTextColor))
    }

    override fun onFieldInvalid(message: String) {
        binding.formCheckboxText.setTextColor(context.colorFrom(R.color.red_error_600))
    }

    private fun setupListeners() {
        binding.formCheckbox.setOnCheckedChangeListener { _, isChecked ->
            model?.isChecked = isChecked
            runValidators()
        }
        binding.formCheckboxText.setOnClickListener {
            binding.formCheckbox.isChecked = model?.isChecked?.not() ?: false
        }
    }

}
