package com.revolhope.presentation.library.component.grocerylist.adapter.view

import android.content.Context
import androidx.core.view.isVisible
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.GroceryListItemViewBinding
import com.revolhope.presentation.library.component.BaseView
import com.revolhope.presentation.library.component.grocerylist.model.GroceryListUiModel
import com.revolhope.presentation.library.extensions.getString
import com.revolhope.presentation.library.extensions.inflater

class GroceryListItemView(context: Context) :
    BaseView<GroceryListUiModel, GroceryListItemViewBinding>(context) {

    override val binding = GroceryListItemViewBinding.inflate(context.inflater, this, true)

    override fun bind(model: GroceryListUiModel) {
        super.bind(model)
        binding.listNameTextView.text = model.name
        binding.numItemsTextView.text = getString(R.string.num_of_items, model.itemNumber)
        binding.updatedOnTextView.text = if (model.updateOn != null) {
            getString(R.string.updated_on, model.updateOn.formatted)
        } else {
            getString(R.string.created_on, model.createdOn.formatted)
        }
        model.currentPrice?.let {
            binding.currentAmountTextView.text = getString(R.string.current_amount, "$it€")
            binding.currentAmountTextView.isVisible = true
        } ?: binding.currentAmountTextView.gone()

        model.purchaseOn?.let {
            binding.nextPurchaseTextView.text = getString(R.string.next_purchase_on, it.formatted)
            binding.nextPurchaseTextView.isVisible = true
        } ?: binding.nextPurchaseTextView.gone()

        binding.isSharedIcon.isVisible = model.isShared
        binding.newItemsIcon.isVisible = model.isNewItems
    }
}
