package com.revolhope.data.feature.grocery.response

data class GroceryItemResponse(
    val id: String?,
    val name: String?,
    val unitPrice: Float?,
    val market: String?,
    val quantity: Int?,
    val isBought: Boolean?,
    val addedBy: String?,
    val addedOn: Long?,
    val updatedOn: Long?,
    val updatedBy: String?
)
