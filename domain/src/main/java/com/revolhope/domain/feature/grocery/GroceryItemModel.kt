package com.revolhope.domain.feature.grocery

import android.os.Parcelable
import com.revolhope.domain.common.model.DateModel
import com.revolhope.domain.common.model.PriceModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroceryItemModel(
    val id: String,
    val name: String,
    val unitPrice: PriceModel?,
    val market: MarketModel?,
    val quantity: Int,
    val isBought: Boolean,
    val addedBy: String,
    val addedOn: DateModel,
    val updatedOn: DateModel?
) : Parcelable {
    val totalPrice: PriceModel?
        get() = unitPrice?.value?.times(quantity)?.let { PriceModel(value = it) }
}
