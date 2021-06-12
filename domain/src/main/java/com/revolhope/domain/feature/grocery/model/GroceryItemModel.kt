package com.revolhope.domain.feature.grocery.model

import android.os.Parcelable
import com.revolhope.domain.common.extensions.randomId
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
    val addedBy: UserSharedModel,
    val addedOn: DateModel,
    val updatedOn: DateModel?,
    val updatedBy: UserSharedModel?
) : Parcelable {
    val totalPrice: PriceModel?
        get() = unitPrice?.value?.times(quantity)?.let { PriceModel(value = it) }

    companion object {
        fun new(
            name: String,
            unitPrice: PriceModel? = null,
            market: MarketModel? = null,
            quantity: Int = 1,
            isBought: Boolean = false,
            addedBy: UserSharedModel,
            addedOn: DateModel = DateModel.today,
            updatedOn: DateModel? = null,
            updatedBy: UserSharedModel? = if (updatedOn != null) addedBy else null
        ) = GroceryItemModel(
            id = randomId(),
            name = name,
            unitPrice = unitPrice,
            market = market,
            quantity = quantity,
            isBought = isBought,
            addedBy = addedBy,
            addedOn = addedOn,
            updatedOn = updatedOn,
            updatedBy = updatedBy
        )
    }
}
