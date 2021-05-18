package com.revolhope.presentation.library.component.snackbar.model

sealed class SnackBarModel(
    open val message: String,
    open val onClick: (() -> Unit)?,
    open val onDismiss: (() -> Unit)?
) {
    data class Success(
        override val message: String,
        override val onClick: (() -> Unit)? = null,
        override val onDismiss: (() -> Unit)? = null
    ) : SnackBarModel(message, onClick, onDismiss)

    data class Error(
        override val message: String,
        override val onClick: (() -> Unit)? = null,
        override val onDismiss: (() -> Unit)? = null
    ) : SnackBarModel(message, onClick, onDismiss)
}
