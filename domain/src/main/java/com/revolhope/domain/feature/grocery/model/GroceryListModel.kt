package com.revolhope.domain.feature.grocery.model

import android.os.Parcelable
import com.revolhope.domain.common.extensions.randomId
import com.revolhope.domain.common.extensions.sumAllTotalPrices
import com.revolhope.domain.common.extensions.sumExistingTotalPrices
import com.revolhope.domain.common.model.DateModel
import com.revolhope.domain.common.model.PriceModel
import com.revolhope.domain.feature.authentication.model.UserModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroceryListModel(
    val id: String,
    var title: String,
    val items: MutableList<GroceryItemModel>,
    val createdOn: DateModel,
    val createdBy: UserSharedModel,
    val updatedOn: DateModel,
    val updatedBy: UserSharedModel,
    var purchaseOn: DateModel,
    var sharedWith: UserSharedModel?
) : Parcelable {
    val isShared: Boolean get() = sharedWith?.id.isNullOrBlank().not()
    val itemCount: Int get() = items.count()
    val currentPriceAll: PriceModel? get() = items.sumAllTotalPrices
    val currentPrice: PriceModel? get() = items.sumExistingTotalPrices
    val markets: List<MarketModel> get() = items.mapNotNull { it.market }.distinctBy { it.id }

    companion object {

        fun new(user: UserModel) : GroceryListModel =
                GroceryListModel(
                    id = randomId(),
                    title = "",
                    items = mutableListOf(),
                    createdOn = DateModel.today,
                    createdBy = user.sharedModel,
                    updatedOn = DateModel.today,
                    updatedBy = user.sharedModel,
                    purchaseOn = DateModel.empty,
                    sharedWith = null
                )

        val empty: GroceryListModel
            get() =
                GroceryListModel(
                    id = "",
                    title = "",
                    items = mutableListOf(),
                    createdOn = DateModel.empty,
                    createdBy = UserSharedModel.empty,
                    updatedOn = DateModel.empty,
                    updatedBy = UserSharedModel.empty,
                    purchaseOn = DateModel.empty,
                    sharedWith = null
                )
    }
}
