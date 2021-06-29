package com.revolhope.presentation.library.component.loader

import android.content.Context
import androidx.annotation.StringRes
import com.revolhope.domain.common.extensions.safeRunOrDefault


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
                        placeholdersRes.safeRunOrDefault(unformatted) {
                            map(context::getString).toTypedArray().let {
                                String.format(unformatted, *it)
                            }
                        }
                    }
                    !placeholders.isNullOrEmpty() -> {
                        placeholders.safeRunOrDefault(unformatted) {
                            String.format(unformatted, *placeholders.toTypedArray())
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
        val empty: LoadingMessageModel
            get() =
                LoadingMessageModel()
    }
}
