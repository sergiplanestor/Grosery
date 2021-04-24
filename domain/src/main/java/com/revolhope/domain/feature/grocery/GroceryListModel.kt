package com.revolhope.domain.feature.grocery

import android.os.Parcelable
import com.revolhope.domain.common.extensions.sumAllTotalPrices
import com.revolhope.domain.common.extensions.sumExistingTotalPrices
import com.revolhope.domain.common.model.DateModel
import com.revolhope.domain.common.model.PriceModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroceryListModel(
    val id: String,
    val title: String,
    val items: List<GroceryItemModel>,
    val createdOn: DateModel,
    val createdBy: String,
    val updatedOn: DateModel,
    val updatedBy: String,
    val purchaseOn: DateModel,
    val sharedWith: String?
): Parcelable {
    val isShared: Boolean get() = sharedWith.isNullOrBlank().not()
    val itemCount: Int get() = items.count()
    val currentPriceAll: PriceModel? get() = items.sumAllTotalPrices
    val currentPrice: PriceModel? get() = items.sumExistingTotalPrices
    val markets: List<MarketModel> get() = items.mapNotNull { it.market }.distinctBy { it.id }
}
