package com.revolhope.presentation.feature.dashboard.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.FragmentDashboardBinding
import com.revolhope.presentation.feature.grocerylist.GroceryListActivity
import com.revolhope.presentation.feature.login.LoginViewModel
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.base.BaseFragment
import com.revolhope.presentation.library.component.grocerylist.model.GroceryListViewUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDashboardBinding = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun initObservers() {
        super.initObservers()
    }

    override fun bindViews() {
        super.bindViews()
        binding.currentGroceryListView.bind(
            GroceryListViewUiModel(
                emptyList(), // TODO Change
                onCreateNewList = { (activity as? BaseActivity)?.run(GroceryListActivity::start) }
            )
        )
    }
}
