package com.revolhope.presentation.feature.dashboard.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.revolhope.domain.common.model.DateModel
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.profile.model.ProfileAvatar
import com.revolhope.domain.feature.profile.model.ProfileModel
import com.revolhope.presentation.databinding.FragmentProfileBinding
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.base.BaseFragment
import com.revolhope.presentation.library.component.profilecard.ProfileCardUiModel
import com.revolhope.presentation.library.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)

    override fun onLoadData() {
        super.onLoadData()
        viewModel.fetchUser()
    }

    override fun initObservers() {
        super.initObservers()
        observe(viewModel.errorLiveData, ::onErrorReceived)
        observe(viewModel.errorResLiveData) { onErrorReceived(getString(it)) }
        observe(viewModel.userLiveData, ::onUserReceived)
    }

    private fun onUserReceived(user: UserModel) {
        // TODO: If shimmer or loader, hide it here
        setupUserCard(user)
        setupUserLastActions()
        setupUserTemplateList()
    }

    private fun setupUserCard(user: UserModel) {
        binding.profileCardView.bind(
            ProfileCardUiModel(
                avatar = ProfileAvatar.OSTRICH,
                username = user.name,
                email = user.email,
                numberOfLists = 3,
                lastConnectedOn = DateModel.today,
                lastUpdatedOn = DateModel.today,
                onEditClick = ::onEditProfile
            )
        )
    }

    private fun setupUserLastActions() {

    }

    private fun setupUserTemplateList() {

    }

    private fun onEditProfile() {
        findNavController().navigate(
            ProfileFragmentDirections.actionToEditProfile(
                // TODO: Remove, test purposes
                ProfileModel(
                    userId = "",
                    email = "",
                    username = "",
                    avatar = ProfileAvatar.CAT,
                    lastContributionOn = DateModel.today,
                    lastConnectionOn = DateModel.today
                ),
                BaseActivity.NavTransition.MODAL
            )
        )
    }
}
