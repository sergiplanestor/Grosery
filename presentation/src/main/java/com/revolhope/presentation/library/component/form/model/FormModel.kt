package com.revolhope.presentation.library.component.form.model

import android.graphics.drawable.Drawable
import android.text.InputType
import com.revolhope.presentation.R

sealed class FormModel(
    open var value: String? = null,
    open val hint: String,
    open val helperText: String? = null,
    open val inputType: Int,
    open val validators: List<FormValidator>,
    open val startDrawable: Drawable? = null,
    open val isRequired: Boolean = true,
    open var isFieldValid: Boolean = false
) {

    data class Text(
        override var value: String? = null,
        override val hint: String,
        override val helperText: String? = null,
        override val inputType: Int = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
        override val isRequired: Boolean = true,
        override val startDrawable: Drawable? = null,
        override val validators: List<FormValidator> = if (isRequired) {
            listOf(FormValidator.Required())
        } else {
            emptyList()
        },
        override var isFieldValid: Boolean = false
    ) : FormModel(
        value = value,
        hint = hint,
        helperText = helperText,
        inputType = inputType,
        validators = validators,
        startDrawable = startDrawable,
        isFieldValid = isFieldValid
    )

    data class Email(
        override var value: String? = null,
        override val hint: String,
        override val helperText: String? = null,
        override val inputType: Int = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
        override val isRequired: Boolean = true,
        override val validators: List<FormValidator> = mutableListOf<FormValidator>().apply {
            add(FormValidator.Email())
            if (isRequired) add(FormValidator.Required())
        },
        override val startDrawable: Drawable? = null,
        override var isFieldValid: Boolean = false
    ) : FormModel(
        value = value,
        hint = hint,
        helperText = helperText,
        inputType = inputType,
        validators = validators,
        startDrawable = startDrawable,
        isFieldValid = isFieldValid
    )

    data class Password(
        override var value: String? = null,
        override val hint: String,
        override val helperText: String? = null,
        override val inputType: Int = InputType.TYPE_TEXT_VARIATION_PASSWORD,
        override val isRequired: Boolean = true,
        override val validators: List<FormValidator> = mutableListOf<FormValidator>().apply {
            add(FormValidator.Password())
            if (isRequired) add(FormValidator.Required())
        },
        override val startDrawable: Drawable? = null,
        override var isFieldValid: Boolean = false
    ) : FormModel(
        value = value,
        hint = hint,
        helperText = helperText,
        inputType = inputType,
        validators = validators,
        startDrawable = startDrawable,
        isFieldValid = isFieldValid
    )

}
