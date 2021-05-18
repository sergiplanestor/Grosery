package com.revolhope.presentation.library.component.form.model

import android.util.Patterns
import androidx.annotation.StringRes
import com.revolhope.presentation.R

sealed class FormValidator(
    open val regex: Regex,
    @StringRes open val errorMessageResource: Int?
) {

    companion object {
        private const val PATTERN_REQUIRED = "^(?!\\s*\$|false\$).+"
        private const val PATTERN_PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}\$"
        private const val PATTERN_NUMBER = "^(.*\\d)\$"
    }

    fun matches(value: String?): Boolean = value?.let(regex::matches) ?: false

    data class Required(
        override val regex: Regex = Regex(PATTERN_REQUIRED),
        override val errorMessageResource: Int? = R.string.form_error_field_required
    ) : FormValidator(regex, errorMessageResource)

    data class Password(
        override val regex: Regex = Regex(PATTERN_PASSWORD),
        override val errorMessageResource: Int? = R.string.form_error_field_password_pattern
    ) : FormValidator(regex, errorMessageResource)

    data class Email(
        override val regex: Regex = Patterns.EMAIL_ADDRESS.toRegex(),
        override val errorMessageResource: Int? = R.string.form_error_field_email_pattern
    ) : FormValidator(regex, errorMessageResource)

    data class Number(
        override val regex: Regex = Regex(PATTERN_NUMBER),
        override val errorMessageResource: Int? = R.string.form_error_field_number_pattern
    ) : FormValidator(regex, errorMessageResource)
}
