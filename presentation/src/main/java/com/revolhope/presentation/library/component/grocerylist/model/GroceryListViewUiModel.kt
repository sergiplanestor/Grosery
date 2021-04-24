package com.revolhope.presentation.library.component.grocerylist.model

data class GroceryListViewUiModel(
    val items: List<GroceryListUiModel>,
    val onListClick: ((GroceryListUiModel) -> Unit)? = null,
    val onCreateNewList: () -> Unit
)
