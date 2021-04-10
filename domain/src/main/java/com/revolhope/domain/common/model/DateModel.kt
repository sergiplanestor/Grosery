package com.revolhope.domain.common.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateModel(
    val value: Long,
    val formatted: String
) : Parcelable {
    companion object {
        val empty: DateModel
            get() =
                DateModel(
                    value = 0L,
                    formatted = ""
                )
    }
}
