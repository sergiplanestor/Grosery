package com.revolhope.presentation.feature.dashboard.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.presentation.databinding.FragmentDashboardBinding
import com.revolhope.presentation.feature.grocerylist.GroceryListActivity
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.base.BaseFragment
import com.revolhope.presentation.library.component.grocerylist.model.GroceryListViewUiModel
import com.revolhope.presentation.library.extensions.observe
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
        observe(viewModel.userLiveData, ::onUserReceived)
    }

    override fun bindViews() {
        super.bindViews()
        binding.currentGroceryListView.bind(
            GroceryListViewUiModel(
                emptyList(), // TODO Change
                onCreateNewList = viewModel::fetchUser
            )
        )
    }

    private fun onUserReceived(user: UserModel) =
        (activity as? BaseActivity)?.run { GroceryListActivity.start(this, user) }
}
