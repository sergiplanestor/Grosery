package com.revolhope.presentation.library.component.loader

import android.content.Context
import androidx.annotation.StringRes


data class LoadingMessageModel(
    val message: String? = null,
    @StringRes val messageRes: Int? = null,
    val placeholders: List<Any>? = null,
    val placeholdersRes: List<Int>? = null
) {
    private val isMessageResource: Boolean get() = messageRes != null

    fun message(context: Context): String? =
        if (isMessageResource) {
            messageRes?.let { context.getString(messageRes) }?.let { unformatted ->
                when {
                    !placeholdersRes.isNullOrEmpty() -> {
                        try {
                            placeholdersRes.map(context::getString).toTypedArray().let {
                                String.format(unformatted, *it)
                            }
                        } catch (e: Exception) {
                            unformatted
                        }
                    }
                    !placeholders.isNullOrEmpty() -> {
                        try {
                            String.format(unformatted, *placeholders.toTypedArray())
                        } catch (e: Exception) {
                            unformatted
                        }
                    }
                    else -> unformatted
                }
            }
        } else {
            message
        }

    fun messageOrEmpty(context: Context): String = message(context).orEmpty()

    companion object {
        val empty: LoadingMessageModel get() =
            LoadingMessageModel()
    }
}
