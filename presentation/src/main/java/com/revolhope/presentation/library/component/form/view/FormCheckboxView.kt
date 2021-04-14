package com.revolhope.presentation.library.component.form.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginBottom
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ComponentFormCheckboxViewBinding
import com.revolhope.presentation.library.component.form.model.FormModel
import com.revolhope.presentation.library.extensions.colorFrom
import com.revolhope.presentation.library.extensions.dimensionFrom
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
            textChanged()
        }

    override fun bind(model: FormModel.Checkbox) {
        super.bind(model)
        binding.formCheckboxText.text = model.hint
        binding.formCheckbox.isChecked = model.isChecked
        binding.formCheckboxText.justify(enableJustify = model.isTextJustified)
        setupListeners()
        textChanged()
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

    private fun textChanged() {
        binding.formCheckboxText.doOnPreDraw {
            binding.formCheckboxText.layoutParams =
                (binding.formCheckboxText.layoutParams as? LayoutParams)?.apply {
                    if (binding.formCheckboxText.lineCount > 1) {
                        bottomToBottom = LayoutParams.UNSET
                        setMargins(
                            marginStart,
                            context.dimensionFrom(R.dimen.margin_small).toInt(),
                            marginEnd,
                            marginBottom
                        )
                    } else {
                        bottomToBottom = binding.formCheckbox.id
                        setMargins(marginStart, 0, marginEnd, marginBottom)
                    }
                }
        }
    }

}
