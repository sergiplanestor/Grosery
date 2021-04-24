package com.revolhope.presentation.library.component.form.view

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import androidx.core.widget.addTextChangedListener
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ComponentFormAmountSelectorViewBinding
import com.revolhope.presentation.library.component.form.model.FormModel
import com.revolhope.presentation.library.extensions.getString
import com.revolhope.presentation.library.extensions.inflater

class FormAmountSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FormView<FormModel.AmountSelector>(context, attrs, defStyleAttr) {

    enum class DataType {
        INT,
        FLOAT
    }

    private val binding =
        ComponentFormAmountSelectorViewBinding.inflate(context.inflater, this, true)

    private var dataType: DataType = DataType.INT

    override fun bind(model: FormModel.AmountSelector) {
        super.bind(model)
        binding.formInputLayout.hint = model.hint
        binding.formEditText.inputType = model.inputType
        model.value?.let { binding.formEditText.setText(it) }
        model.helperText?.let { binding.formInputLayout.helperText = it }
        setupListeners()
    }

    private fun setupListeners() {
        binding.formInputLayout.setStartIconOnClickListener {
            binding.formEditText.text?.operate(increase = false)?.let {
                binding.formEditText.setText(it.toString())
            }
        }
        binding.formInputLayout.setEndIconOnClickListener {
            binding.formEditText.text?.operate(increase = true)?.let {
                binding.formEditText.setText(it.toString())
            }
        }
        binding.formEditText.addTextChangedListener(
            afterTextChanged = { editable ->
                editable?.toString()?.toFloatOrNull()?.let {
                    model?.amount = it
                } ?: onFieldInvalid(getString(R.string.form_error_field_number_pattern))
            }
        )
    }

    override fun onFieldValid() {
        binding.formEditText.error = null
    }

    override fun onFieldInvalid(message: String) {
        binding.formEditText.error = message
    }

    private fun Editable?.operate(increase: Boolean): Number? {
        val intValue: Int?
        val floatValue: Float?
        return when (dataType) {
            DataType.INT -> {
                intValue = toString().toIntOrNull()
                if (intValue == null) {
                    floatValue = toString().toFloatOrNull()
                    if (floatValue == null) {
                        null
                    } else {
                        dataType = DataType.FLOAT
                        when {
                            increase -> floatValue + 1
                            floatValue > 1 -> floatValue - 1
                            else -> 0
                        }
                    }
                } else {
                    when {
                        increase -> intValue + 1
                        intValue > 1 -> intValue - 1
                        else -> 0
                    }
                }
            }
            DataType.FLOAT -> {
                floatValue = toString().toFloatOrNull()
                if (floatValue == null) {
                    intValue = toString().toIntOrNull()
                    if (intValue == null) {
                        null
                    } else {
                        dataType = DataType.INT
                        when {
                            increase -> intValue + 1
                            intValue > 1 -> intValue - 1
                            else -> 0
                        }
                    }
                } else {
                    when {
                        increase -> floatValue + 1
                        floatValue > 1 -> floatValue - 1
                        else -> 0
                    }
                }
            }
        }
    }
}
