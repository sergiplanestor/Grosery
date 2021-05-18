package com.revolhope.data.feature.grocery.response

data class GroceryListResponse(
    val id: String?,
    val title: String?,
    val items: List<GroceryItemResponse>?,
    val createdOn: Long?,
    val createdBy: String?,
    val updatedOn: Long?,
    val updatedBy: String?,
    val purchaseOn: Long?,
    val sharedWith: String?
)
