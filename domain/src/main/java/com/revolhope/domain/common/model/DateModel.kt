package com.revolhope.domain.common.model

import android.os.Parcelable
import com.revolhope.domain.common.extensions.toDateFormat
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateModel(
    val value: Long
) : Parcelable {
    companion object {
        val empty: DateModel
            get() =
                DateModel(
                    value = 0L,
                )
    }

    val formatted: String? = value.toDateFormat()
}
