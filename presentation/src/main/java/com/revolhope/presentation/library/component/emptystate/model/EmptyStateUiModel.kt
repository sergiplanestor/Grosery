package com.revolhope.presentation.library.component.emptystate.model

import android.content.Context
import com.revolhope.presentation.R

data class EmptyStateUiModel(
    val title: String? = null,
    val message: String? = null,
    val actionName: String? = null,
    val action: (() -> Unit)? = null
) {
    fun titleOrDefault(context: Context): String =
        title ?: context.getString(R.string.oops)
}
