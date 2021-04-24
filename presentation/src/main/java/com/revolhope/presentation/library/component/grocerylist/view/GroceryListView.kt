package com.revolhope.presentation.library.component.grocerylist.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ComponentGroceryListViewBinding
import com.revolhope.presentation.library.component.BaseView
import com.revolhope.presentation.library.component.emptystate.model.EmptyStateUiModel
import com.revolhope.presentation.library.component.grocerylist.adapter.GroceryListViewAdapter
import com.revolhope.presentation.library.component.grocerylist.model.GroceryListUiModel
import com.revolhope.presentation.library.component.grocerylist.model.GroceryListViewUiModel
import com.revolhope.presentation.library.extensions.getString
import com.revolhope.presentation.library.extensions.inflater

class GroceryListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseView<GroceryListViewUiModel, ComponentGroceryListViewBinding>(
    context,
    attrs,
    defStyleAttr
) {

    val binding = ComponentGroceryListViewBinding.inflate(context.inflater, this, true)
    private lateinit var adapter: GroceryListViewAdapter

    override fun bind(model: GroceryListViewUiModel) {
        model.items.isEmpty().let {
            binding.recyclerView.isVisible = it.not()
            binding.cardEmptyStateContainer.isVisible = it
            if (it) {
                initEmptyState(model.onCreateNewList)
            } else {
                initContentRecycler(model.items, model.onListClick)
            }
        }
    }

    private fun initContentRecycler(
        items: List<GroceryListUiModel>,
        onListClick: ((GroceryListUiModel) -> Unit)?
    ) {
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter =
                GroceryListViewAdapter(items.toMutableList(), onListClick).also { adapter = it }
        }
    }

    private fun initEmptyState(onEmptyActionClick: () -> Unit) {
        binding.emptyStateView.bind(
            EmptyStateUiModel(
                message = getString(R.string.no_grocery_list_message),
                actionName = getString(R.string.new_list),
                action = onEmptyActionClick
            )
        )
    }

    fun update(items: List<GroceryListUiModel>) { adapter.update(items) }
}
