package com.revolhope.presentation.feature.grocerylist

import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import com.revolhope.presentation.databinding.ActivityGroceryListBinding
import com.revolhope.presentation.feature.grocerylist.form.CompleteFormAdapter
import com.revolhope.presentation.feature.grocerylist.form.GroceryFormUiModel
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.component.form.model.FormModel
import com.revolhope.presentation.library.extensions.alphaAnimation
import com.revolhope.presentation.library.extensions.expandCollapseAnimation
import com.revolhope.presentation.library.extensions.rotate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroceryListActivity : BaseActivity() {

    private lateinit var binding: ActivityGroceryListBinding
    private lateinit var adapter: CompleteFormAdapter
    private var isExpanding = false
    private var expandedHeight: Int? = null
    private var collapsedHeight: Int? = null

    companion object {
        private const val EXTRA_GROCERY_LIST = "grocery.item"
        fun start(activity: BaseActivity) {
            activity.startActivity(
                Intent(activity, GroceryListActivity::class.java).apply {
                    putExtras(
                        bundleOf(
                            EXTRA_NAVIGATION_TRANSITION to NavTransition.MODAL,
                            EXTRA_GROCERY_LIST to "" //TODO: Change
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

        bindToolbar()
        bindCompleteForm()
        binding.inputLayoutContainer.doOnLayout { expandedHeight = it.height }
        binding.expandCollapseButton.setOnClickListener { onClickExpandCollapse() }
    }

    private fun bindToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "T_New Item :D"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    private fun bindCompleteForm() {
        binding.completeFormViewPager.adapter = CompleteFormAdapter(
            mutableListOf(
                GroceryFormUiModel(formModels = listOf(
                    FormModel.Text(hint = "Name"),
                    FormModel.AmountSelector(hint = "Amount")
                )),
                GroceryFormUiModel(formModels = listOf(
                    FormModel.Text(hint = "Price"),
                    FormModel.Text(hint = "Market")
                ))
            )
        ).also { adapter = it }
    }

    private fun onClickExpandCollapse() {
        if (isExpanding) {
            expandedHeight?.let {
                binding.inputLayoutContainer.expandCollapseAnimation(
                    targetHeight = it,
                    isExpanding = true,
                    onStart = {
                        binding.simpleFormBinding.simpleFormLayoutContainer.alphaAnimation(
                            isShowing = false,
                            onEnd = {
                                binding.completeFormViewPager.alphaAnimation(
                                    isShowing = true
                                )
                            }
                        )
                    }
                )
            }
        } else {
            if (collapsedHeight != null) {
                binding.inputLayoutContainer.expandCollapseAnimation(
                    targetHeight = collapsedHeight!!,
                    isExpanding = false,
                    onStart = {
                        binding.completeFormViewPager.alphaAnimation(
                            isShowing = false,
                            onEnd = {
                                binding.simpleFormBinding.simpleFormLayoutContainer.alphaAnimation(
                                    isShowing = true
                                )
                            }
                        )
                    }
                )
            } else {
                binding.completeFormViewPager.alphaAnimation(
                    isShowing = false,
                    onEnd = {
                        binding.simpleFormBinding.simpleFormLayoutContainer.alphaAnimation(
                            isShowing = true
                        )
                    }
                )
                binding.inputLayoutContainer.doOnPreDraw { collapsedHeight = it.height }
            }
        }
        binding.expandCollapseButton.rotate(if (isExpanding) -180f else 180f)
        isExpanding = isExpanding.not()
    }
}
