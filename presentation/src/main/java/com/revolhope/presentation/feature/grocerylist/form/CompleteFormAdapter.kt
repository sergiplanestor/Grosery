package com.revolhope.presentation.feature.grocerylist.form

import android.view.ViewGroup
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.GroceryListExtraFormViewBinding
import com.revolhope.presentation.databinding.GroceryListSimpleFormViewBinding
import com.revolhope.presentation.library.base.DiffUtilAdapter
import com.revolhope.presentation.library.component.form.model.FormModel
import com.revolhope.presentation.library.extensions.inflater

class CompleteFormAdapter(
    override val items: MutableList<GroceryFormUiModel>
) : DiffUtilAdapter<GroceryFormUiModel, ViewGroup>(items) {

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): ViewGroup =
        parent.context.inflater.inflate(
            when (viewType) {
                ViewType.SIMPLE.id -> {
                    R.layout.grocery_list_simple_form_view
                }
                else /* ViewType.COMPLETE.id */ -> {
                    R.layout.grocery_list_extra_form_view
                }
            },
            parent,
            false
        ) as ViewGroup

    override fun getItemViewType(position: Int): Int = ViewType.fromPosition(position).id

    override fun onBindView(view: ViewGroup, position: Int) {
        when (ViewType.fromPosition(position)) {
            ViewType.SIMPLE -> {
                GroceryListSimpleFormViewBinding.bind(view).apply {
                    itemNameFormInput.bind(items[position].formModels.first())
                    itemAmountFormInput.bind(items[position].formModels.last() as FormModel.AmountSelector)
                }
            }
            ViewType.COMPLETE -> {
                GroceryListExtraFormViewBinding.bind(view).apply {
                    itemPriceFormInput.bind(items[position].formModels.first())
                    marketFormInput.bind(items[position].formModels.last())
                }
            }
        }
    }

    override fun areItemsTheSame(
        oldItem: GroceryFormUiModel,
        newItem: GroceryFormUiModel
    ): Boolean = oldItem === newItem

    override fun areContentsTheSame(
        oldItem: GroceryFormUiModel,
        newItem: GroceryFormUiModel
    ): Boolean = oldItem.formModels == newItem.formModels

    enum class ViewType(val id: Int, val position: Int) {
        SIMPLE(0, 0),
        COMPLETE(1, 1);

        companion object {
            fun fromPosition(position: Int): ViewType =
                values().associateBy { it.position }[position] ?: SIMPLE
        }
    }
}
