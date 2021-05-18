package com.revolhope.presentation.library.component.form.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import com.revolhope.presentation.library.component.form.model.FormModel

abstract class FormView<T : FormModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val EMPTY_ERROR = " "
    }

    protected var model: T? = null

    val value: String? get() = model?.value

    @CallSuper
    open fun bind(model: T) {
        this.model = model
        applyMargins(model.margins)
    }

    abstract fun onFieldValid()

    abstract fun onFieldInvalid(message: String)

    fun runValidators(): Boolean =
        model?.let { formModel ->
            for (validator in formModel.validators) {
                formModel.isFieldValid = validator.matches(formModel.value)
                if (!formModel.isFieldValid) {
                    onFieldInvalid(
                        validator.errorMessageResource?.let(context::getString) ?: EMPTY_ERROR
                    )
                    break
                }
            }
            if (formModel.isFieldValid) onFieldValid()
            formModel.isFieldValid
        } ?: false

    protected open fun applyMargins(margins: Map<FormModel.Margin, Int>) {
        layoutParams = (layoutParams as LayoutParams).apply {
            margins.keys.forEach { marginEnum ->
                margins[marginEnum]?.let { margin ->
                    when (marginEnum) {
                        FormModel.Margin.TOP -> topMargin = margin
                        FormModel.Margin.START -> marginStart = margin
                        FormModel.Margin.END -> marginEnd = margin
                        FormModel.Margin.BOTTOM -> bottomMargin = margin
                        FormModel.Margin.HORIZONTAL -> {
                            marginStart = margin
                            marginEnd = margin
                        }
                        FormModel.Margin.VERTICAL -> {
                            topMargin = margin
                            bottomMargin = margin
                        }
                    }
                }
            }
        }
    }
}
