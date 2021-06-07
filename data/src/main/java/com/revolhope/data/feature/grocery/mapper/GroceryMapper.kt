package com.revolhope.data.feature.grocery.mapper

import com.revolhope.data.common.extensions.asJson
import com.revolhope.data.common.extensions.fromJsonTo
import com.revolhope.data.common.extensions.fromJsonToSafe
import com.revolhope.data.common.extensions.mapToMutable
import com.revolhope.data.common.price.PriceMapper
import com.revolhope.data.feature.grocery.response.GroceryItemResponse
import com.revolhope.data.feature.grocery.response.GroceryListResponse
import com.revolhope.domain.common.model.asDateModelOrEmpty
import com.revolhope.domain.feature.grocery.model.GroceryItemModel
import com.revolhope.domain.feature.grocery.model.GroceryListModel
import com.revolhope.domain.feature.grocery.model.MarketModel
import com.revolhope.domain.feature.grocery.model.UserSharedModel

object GroceryMapper {

    fun fromListResponseToModel(
        response: GroceryListResponse
    ): GroceryListModel =
        GroceryListModel(
            id = response.id.orEmpty(),
            title = response.title.orEmpty(),
            items = response.items?.mapToMutable(::fromItemResponseToModel) ?: mutableListOf(),
            createdOn = response.createdOn.asDateModelOrEmpty(),
            createdBy = response.createdBy.fromJsonToSafe(UserSharedModel::class, UserSharedModel.empty),
            updatedOn = response.updatedOn.asDateModelOrEmpty(),
            updatedBy = response.updatedBy.fromJsonToSafe(UserSharedModel::class, UserSharedModel.empty),
            purchaseOn = response.purchaseOn.asDateModelOrEmpty(),
            sharedWith = response.sharedWith?.fromJsonTo(UserSharedModel::class)
        )

    private fun fromItemResponseToModel(response: GroceryItemResponse): GroceryItemModel =
        GroceryItemModel(
            id = response.id.orEmpty(),
            name = response.name.orEmpty(),
            unitPrice = response.unitPrice?.let(PriceMapper::parseToModel),
            market = response.market?.fromJsonTo(MarketModel::class),
            quantity = response.quantity ?: 1,
            isBought = response.isBought == true,
            addedBy = response.addedBy.fromJsonToSafe(UserSharedModel::class, UserSharedModel.empty),
            addedOn = response.addedOn.asDateModelOrEmpty(),
            updatedOn = response.updatedOn.asDateModelOrEmpty(),
            updatedBy = response.updatedBy?.fromJsonTo(UserSharedModel::class)
        )

    fun fromListModelToResponse(model: GroceryListModel): GroceryListResponse =
        GroceryListResponse(
            id = model.id,
            title = model.title,
            items = model.items.map(::fromItemModelToResponse),
            createdOn = model.createdOn.value,
            createdBy = model.createdBy.asJson(),
            updatedOn = model.updatedOn.value,
            updatedBy = model.updatedBy.asJson(),
            purchaseOn = model.purchaseOn.value,
            sharedWith = model.sharedWith?.asJson()
        )

    private fun fromItemModelToResponse(model: GroceryItemModel): GroceryItemResponse =
        GroceryItemResponse(
            id = model.id,
            name = model.name,
            unitPrice = model.unitPrice?.value,
            market = model.market?.asJson(),
            quantity = model.quantity,
            isBought = model.isBought,
            addedBy = model.addedBy.asJson(),
            addedOn = model.addedOn.value,
            updatedOn = model.updatedOn?.value,
            updatedBy = model.updatedBy?.asJson()
        )
}
