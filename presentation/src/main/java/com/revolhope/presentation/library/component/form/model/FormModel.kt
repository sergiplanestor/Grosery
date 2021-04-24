package com.revolhope.presentation.library.component.form.model

import android.graphics.drawable.Drawable
import android.nfc.FormatException
import android.text.InputType
import com.revolhope.presentation.R
import com.revolhope.presentation.library.extensions.toBooleanOrNull

sealed class FormModel(
    open var value: String? = null,
    open val hint: String,
    open val helperText: String? = null,
    open val keyboard: Keyboard,
    open val validators: List<FormValidator>,
    open val startDrawable: Drawable? = null,
    open val isRequired: Boolean = true,
    open var isFieldValid: Boolean = false
) {

    val inputType: Int get() = keyboard.inputType

    enum class Keyboard(val inputType: Int) {
        TEXT(inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES),
        EMAIL(inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS),
        PASSWORD(inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD),
        NUMBER(inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL),
        NONE(inputType = Int.MIN_VALUE)
    }

    data class Text(
        override var value: String? = null,
        override val hint: String,
        override val helperText: String? = null,
        override val keyboard: Keyboard = Keyboard.TEXT,
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
        keyboard = keyboard,
        validators = validators,
        startDrawable = startDrawable,
        isFieldValid = isFieldValid
    )

    data class Email(
        override var value: String? = null,
        override val hint: String,
        override val helperText: String? = null,
        override val keyboard: Keyboard = Keyboard.EMAIL,
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
        keyboard = keyboard,
        validators = validators,
        startDrawable = startDrawable,
        isFieldValid = isFieldValid
    )

    data class Password(
        override var value: String? = null,
        override val hint: String,
        override val helperText: String? = null,
        override val keyboard: Keyboard = Keyboard.PASSWORD,
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
        keyboard = keyboard,
        validators = validators,
        startDrawable = startDrawable,
        isFieldValid = isFieldValid
    )

    data class Checkbox(
        var isChecked: Boolean = false,
        val isTextJustified: Boolean = true,
        override val hint: String,
        override val helperText: String? = null,
        override val keyboard: Keyboard = Keyboard.NONE,
        override val isRequired: Boolean = true,
        override val validators: List<FormValidator> = if (isRequired) {
            listOf(FormValidator.Required())
        } else {
            emptyList()
        },
        override val startDrawable: Drawable? = null,
        override var isFieldValid: Boolean = false
    ) : FormModel(
        hint = hint,
        helperText = helperText,
        keyboard = keyboard,
        validators = validators,
        startDrawable = startDrawable,
        isFieldValid = isFieldValid
    ) {
        override var value: String? = isChecked.toString()
            get() = isChecked.toString()
            set(value) {
                value.toBooleanOrNull()?.let {
                    field = it.toString()
                    isChecked = it
                } ?: (throw FormatException("Class/FormModel: Only boolean values allowed"))
            }
    }

    data class AmountSelector(
        override val hint: String,
        override val helperText: String? = null,
        override val keyboard: Keyboard = Keyboard.NUMBER,
        override val isRequired: Boolean = true,
        override val startDrawable: Drawable? = null,
        override val validators: List<FormValidator> = mutableListOf<FormValidator>().apply {
            add(FormValidator.Number())
            if (isRequired) add(FormValidator.Required())
        },
        override var isFieldValid: Boolean = false
    ) : FormModel(
        hint = hint,
        helperText = helperText,
        keyboard = keyboard,
        validators = validators,
        startDrawable = startDrawable,
        isFieldValid = isFieldValid
    ) {
        override var value: String? = "1"
            set(value) {
                field = if (value != null) {
                    value.toFloatOrNull()?.toString() ?: (throw FormatException("Class/FormModel: Only number values allowed"))
                } else {
                    "0"
                }
            }

        var amount: Float = value?.toFloatOrNull() ?: 0f
            set(value) {
                field = value
                this.value = value.toString()
            }
    }

    data class DropdownSelector<T>(
        val items: List<T>,
        override var value: String? = null,
        override val hint: String,
        override val helperText: String? = null,
        override val keyboard: Keyboard = Keyboard.NONE,
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
        keyboard = keyboard,
        validators = validators,
        startDrawable = startDrawable,
        isFieldValid = isFieldValid
    )
}
