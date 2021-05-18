package com.revolhope.data.feature.grocery.mapper

import com.revolhope.data.common.date.DateMapper
import com.revolhope.data.common.extensions.fromJSON
import com.revolhope.data.common.extensions.mapToMutable
import com.revolhope.data.common.extensions.json
import com.revolhope.data.common.price.PriceMapper
import com.revolhope.data.feature.grocery.response.GroceryItemResponse
import com.revolhope.data.feature.grocery.response.GroceryListResponse
import com.revolhope.domain.common.model.DateModel
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
            createdOn = response.createdOn?.let(DateMapper::parseToModel) ?: DateModel.empty,
            createdBy = response.createdBy?.fromJSON(UserSharedModel::class) ?: UserSharedModel.empty,
            updatedOn = response.updatedOn?.let(DateMapper::parseToModel) ?: DateModel.empty,
            updatedBy = response.updatedBy?.fromJSON(UserSharedModel::class) ?: UserSharedModel.empty,
            purchaseOn = response.purchaseOn?.let(DateMapper::parseToModel) ?: DateModel.empty,
            sharedWith = response.sharedWith?.fromJSON(UserSharedModel::class)
        )

    private fun fromItemResponseToModel(response: GroceryItemResponse): GroceryItemModel =
        GroceryItemModel(
            id = response.id.orEmpty(),
            name = response.name.orEmpty(),
            unitPrice = response.unitPrice?.let(PriceMapper::parseToModel),
            market = response.market?.fromJSON(MarketModel::class),
            quantity = response.quantity ?: 1,
            isBought = response.isBought == true,
            addedBy = response.addedBy?.fromJSON(UserSharedModel::class) ?: UserSharedModel.empty,
            addedOn = response.addedOn?.let(DateMapper::parseToModel) ?: DateModel.empty,
            updatedOn = response.updatedOn?.let(DateMapper::parseToModel) ?: DateModel.empty,
            updatedBy = response.updatedBy?.fromJSON(UserSharedModel::class)
        )

    fun fromListModelToResponse(model: GroceryListModel): GroceryListResponse =
        GroceryListResponse(
            id = model.id,
            title = model.title,
            items = model.items.map(::fromItemModelToResponse),
            createdOn = model.createdOn.value,
            createdBy = model.createdBy.json,
            updatedOn = model.updatedOn.value,
            updatedBy = model.updatedBy.json,
            purchaseOn = model.purchaseOn.value,
            sharedWith = model.sharedWith?.json
        )

    private fun fromItemModelToResponse(model: GroceryItemModel): GroceryItemResponse =
        GroceryItemResponse(
            id = model.id,
            name = model.name,
            unitPrice = model.unitPrice?.value,
            market = model.market?.json,
            quantity = model.quantity,
            isBought = model.isBought,
            addedBy = model.addedBy.json,
            addedOn = model.addedOn.value,
            updatedOn = model.updatedOn?.value,
            updatedBy = model.updatedBy?.json
        )
}
