package com.revolhope.domain.feature.grocery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.util.Base64
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MarketModel(
    val id: String,
    val name: String,
    val imageBase64: String?
) : Parcelable {

    val image: Bitmap? get() = Base64.decode(imageBase64, Base64.DEFAULT)?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }

}
