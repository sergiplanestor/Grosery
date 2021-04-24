package com.revolhope.presentation.library.component.grocerylist.model

import com.revolhope.domain.common.model.DateModel

data class GroceryListUiModel(
    val id: String,
    val name: String,
    val items: List<GroceryItemUiModel>,
    val usernameOwner: String,
    val usernameSharedWith: String?,
    val createdOn: DateModel,
    val updateOn: DateModel?,
    val updateBy: String?,
    val purchaseOn: DateModel?,
    val isNewItems: Boolean
) {
    val isShared: Boolean get() = usernameSharedWith.isNullOrBlank().not()
    val itemNumber: Int get() = items.count()
    val currentPrice: Float? get() =
        if (items.all { it.price != null }) {
            items.sumByDouble { it.price!!.toDouble() }.toFloat()
        } else {
            null
        }
}
