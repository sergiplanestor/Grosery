package com.revolhope.presentation.library.component.grocerylist.adapter

import android.view.ViewGroup
import com.revolhope.presentation.library.base.DiffUtilAdapter
import com.revolhope.presentation.library.component.grocerylist.adapter.view.GroceryListItemView
import com.revolhope.presentation.library.component.grocerylist.model.GroceryListUiModel

class GroceryListViewAdapter(
    override val items: MutableList<GroceryListUiModel>,
    private val onListClick: ((item: GroceryListUiModel) -> Unit)?
) : DiffUtilAdapter<GroceryListUiModel, GroceryListItemView>(items) {

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): GroceryListItemView =
        GroceryListItemView(parent.context)

    override fun onBindView(view: GroceryListItemView, item: GroceryListUiModel) {
        view.bind(model = item)
        view.setOnClickListener { onListClick?.invoke(item) }
    }

    override fun areItemsTheSame(
        oldItem: GroceryListUiModel,
        newItem: GroceryListUiModel
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: GroceryListUiModel,
        newItem: GroceryListUiModel
    ): Boolean = oldItem.id == newItem.id
}
