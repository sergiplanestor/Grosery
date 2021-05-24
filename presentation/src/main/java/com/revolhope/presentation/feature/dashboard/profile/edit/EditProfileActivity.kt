package com.revolhope.presentation.feature.dashboard.profile.edit

import android.view.View
import androidx.activity.viewModels
import androidx.navigation.navArgs
import com.revolhope.presentation.databinding.ActivityEditProfileBinding
import com.revolhope.presentation.feature.dashboard.profile.edit.avatar.fragment.EditAvatarBottomSheet
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.extensions.avatar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val args: EditProfileActivityArgs by navArgs()
    private val viewModel: EditProfileViewModel by viewModels()

    override fun inflateView(): View =
        ActivityEditProfileBinding.inflate(layoutInflater).also { binding = it }.root

    override fun bindViews() {
        super.bindViews()
        applyExitNavAnimation(args.navTransition)
        binding.avatarImageView.avatar = args.profileModel.avatar
        binding.editAvatarButton.setOnClickListener {
            EditAvatarBottomSheet.start(this, args.profileModel)
        }
    }
}
