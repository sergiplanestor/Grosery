package com.revolhope.presentation.library.component.grocerylist.model


data class GroceryItemUiModel(
    val id: String,
    val price: String?,
    val marketName: String?,
    val quantity: Int,
    var isBought: Boolean
)
