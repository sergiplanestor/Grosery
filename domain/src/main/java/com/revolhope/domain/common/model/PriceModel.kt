package com.revolhope.domain.common.model

import android.os.Parcelable
import com.revolhope.domain.common.extensions.priceFormat
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceModel(val value: Float) : Parcelable {

    val formatted: String? get() = value.priceFormat
}
