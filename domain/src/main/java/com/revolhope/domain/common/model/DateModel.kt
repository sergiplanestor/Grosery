package com.revolhope.domain.common.model

data class DateModel(
    val value: Long,
    val formatted: String
) {
    companion object {
        val empty: DateModel
            get() =
            DateModel(
                value = 0L,
                formatted = ""
            )
    }
}