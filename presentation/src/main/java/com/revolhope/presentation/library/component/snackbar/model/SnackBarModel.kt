package com.revolhope.presentation.library.component.snackbar.model

sealed class SnackBarModel(
    open val message: String,
    open val onClick: (() -> Unit)?
) {

    data class Success(
        override val message: String,
        override val onClick: (() -> Unit)? = null
    ) : SnackBarModel(message, onClick)

    data class Error(
        override val message: String,
        override val  onClick: (() -> Unit)? = null
    ) : SnackBarModel(message, onClick)
}