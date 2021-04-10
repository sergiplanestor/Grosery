package com.revolhope.presentation.library.component.form.view

import android.content.Context
import android.util.AttributeSet
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

    @CallSuper
    open fun bind(model: T) {
        this.model = model
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

}
