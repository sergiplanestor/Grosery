package com.revolhope.presentation.feature.grocerylist

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.revolhope.domain.feature.grocery.model.GroceryItemModel
import com.revolhope.domain.feature.grocery.model.GroceryListModel
import com.revolhope.domain.feature.grocery.model.UserSharedModel
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ActivityGroceryListBinding
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.component.form.model.FormModel
import com.revolhope.presentation.library.extensions.dp
import com.revolhope.presentation.library.extensions.or
import com.revolhope.presentation.library.extensions.rotate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroceryListActivity : BaseActivity() {

    private val viewModel: GroceryListViewModel by viewModels()

    private lateinit var binding: ActivityGroceryListBinding
    private val user: UserModel? by lazy {
        intent.extras?.getParcelable(EXTRA_USER) as? UserModel
    }
    private val grocery: GroceryListModel? by lazy {
        intent.extras?.getParcelable(EXTRA_GROCERY_LIST) as? GroceryListModel
    }
    private var isSimpleFormVisible = true

    companion object {
        private const val EXTRA_USER = "grocery.list.user"
        private const val EXTRA_GROCERY_LIST = "grocery.list.list"
        fun start(activity: BaseActivity, user: UserModel, list: GroceryItemModel? = null) {
            activity.startActivity(
                Intent(activity, GroceryListActivity::class.java).apply {
                    putExtras(
                        bundleOf(
                            EXTRA_NAVIGATION_TRANSITION to NavTransition.MODAL,
                            EXTRA_USER to user,
                            EXTRA_GROCERY_LIST to list
                        )
                    )
                }
            )
        }
    }

    override fun inflateView(): View =
        ActivityGroceryListBinding.inflate(layoutInflater).let {
            binding = it
            it.root
        }

    override fun bindViews() {
        super.bindViews()
        viewModel.user = user
        bindToolbar()
        // Bind forms  TODO: review this login
        bindSimpleForm()
        bindCompleteForm()
        // Bind listeners
        bindListeners()
    }

    private fun bindToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = grocery?.title.or(
            context = this,
            stringRes = R.string.new_list
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.grocery_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    private fun bindSimpleForm() {
        binding.simpleFormBinding.itemNameFormInput.bind(
            FormModel.Text(
                hint = "Name",
                margins = emptyMap()
            )
        )
        binding.simpleFormBinding.itemAmountFormInput.bind(
            FormModel.AmountSelector(
                hint = "Amount",
                margins = emptyMap()
            )
        )
        binding.simpleFormBinding.addItemButton.layoutParams =
            (binding.simpleFormBinding.addItemButton.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                bottomMargin = 16.dp
            }
    }

    private fun bindCompleteForm() {
        binding.completeFormBinding.itemNameFormInput.bind(
            FormModel.Text(
                hint = "Name",
                margins = emptyMap()
            )
        )
        binding.completeFormBinding.itemPriceFormInput.bind(
            FormModel.Text(
                hint = "Price",
                margins = emptyMap()
            )
        )
        binding.completeFormBinding.marketFormInput.bind(
            FormModel.Text(
                hint = "Market",
                margins = emptyMap()
            )
        )
        binding.completeFormBinding.itemAmountFormInput.bind(
            FormModel.AmountSelector(
                hint = "Amount",
                margins = emptyMap()
            )
        )
        binding.completeFormBinding.addItemButton.layoutParams =
            (binding.simpleFormBinding.addItemButton.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                bottomMargin = 16.dp
            }
    }

    private fun bindListeners() {
        binding.expandCollapseButton.setOnClickListener { onExpandCollapseClick() }
        binding.simpleFormBinding.addItemButton.setOnClickListener {
            buildGroceryItem()?.let { viewModel.addItem(it) }
        }
        binding.completeFormBinding.addItemButton.setOnClickListener {
            buildGroceryItem()?.let { viewModel.addItem(it) }
        }
    }

    private fun onExpandCollapseClick() {
        if (isSimpleFormVisible) {
            // TODO: Deep Review of this shit
            binding.simpleFormBinding.simpleFormLayoutContainer.isVisible = false
            binding.completeFormBinding.completeFormLayoutContainer.isVisible = true
            // onExpand()
        } else {
            binding.simpleFormBinding.simpleFormLayoutContainer.isVisible = true
            binding.completeFormBinding.completeFormLayoutContainer.isVisible = false
            // onCollapse()
        }
        binding.expandCollapseButton.rotate(angle = if (isSimpleFormVisible) -180f else 180f)
        isSimpleFormVisible = !isSimpleFormVisible
    }

    private val isFormValid: Boolean
        get() = if (isSimpleFormVisible) isSimpleFormValid else isCompleteFormValid

    private val isSimpleFormValid: Boolean
        get() =
            binding.simpleFormBinding.itemNameFormInput.runValidators() &&
                    binding.simpleFormBinding.itemAmountFormInput.runValidators()


    private val isCompleteFormValid: Boolean
        get() =
            binding.completeFormBinding.itemNameFormInput.runValidators() &&
                    binding.completeFormBinding.itemAmountFormInput.runValidators()

    private val formNameValue: String
        get() =
            if (isSimpleFormVisible) {
                binding.simpleFormBinding.itemNameFormInput.value
            } else {
                binding.completeFormBinding.itemNameFormInput.value
            }.orEmpty()

    private val formQuantityValue: Int
        get() =
            if (isSimpleFormVisible) {
                binding.simpleFormBinding.itemAmountFormInput.value
            } else {
                binding.completeFormBinding.itemAmountFormInput.value
            }?.toIntOrNull() ?: 1

    private val formMarketValue: String
        get() =
            if (isSimpleFormVisible) {
                ""
            } else {
                ""
            }

    private val formUnitPriceValue: String
        get() =
            if (isSimpleFormVisible) {
                ""
            } else {
                ""
            }

    private fun buildGroceryItem(): GroceryItemModel? =
        if (isFormValid) {
            GroceryItemModel.new(
                name = formNameValue,
                quantity = formQuantityValue,
                market = null/*formMarketValue*/,
                unitPrice = null/*formUnitPriceValue*/,
                addedBy = user?.sharedModel ?: UserSharedModel.empty
            )
        } else {
            // TODO: Feedback?
            null
        }

    override fun onPause() {
        super.onPause()
        // Persist changes
        viewModel.persistChanges()
    }
}
