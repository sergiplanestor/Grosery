package com.revolhope.presentation.feature.dashboard.profile.edit.avatar.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.revolhope.domain.feature.profile.model.ProfileAvatar
import com.revolhope.domain.feature.profile.model.ProfileModel
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.FragmentEditAvatatBottomSheetBinding
import com.revolhope.presentation.feature.dashboard.profile.edit.avatar.adapter.AvatarsAdapter
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.base.BaseBottomSheetFragment
import com.revolhope.presentation.library.extensions.ClickableViewUiModel
import com.revolhope.presentation.library.extensions.DrawablePosition
import com.revolhope.presentation.library.extensions.TextDrawableUiModel
import com.revolhope.presentation.library.extensions.colorOf
import com.revolhope.presentation.library.extensions.dimensionOf
import com.revolhope.presentation.library.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAvatarBottomSheet : BaseBottomSheetFragment<FragmentEditAvatatBottomSheetBinding>() {

    companion object {
        private const val EXTRA_PROFILE = "profile.edit.avatar.profile_model"
        fun start(activity: BaseActivity, profile: ProfileModel) {
            EditAvatarBottomSheet()
                .apply { arguments = bundleOf(EXTRA_PROFILE to profile) }
                .show(activity.supportFragmentManager)
        }
    }

    private val viewModel: EditAvatarViewModel by viewModels()
    private var adapter: AvatarsAdapter? = null
    private val profile: ProfileModel? by lazy { arguments?.getParcelable(EXTRA_PROFILE) }

    override val title: CharSequence
        get() = getString(R.string.profile_edit_avatar)

    override val doneButtonUiModel: ClickableViewUiModel
        get() = ClickableViewUiModel(
            text = getString(R.string.done),
            drawableUiModel = TextDrawableUiModel(
                drawableRes = R.drawable.ic_tick,
                tintColorInt = context?.colorOf(R.color.successColor),
                paddingDps = context?.dimensionOf(R.dimen.margin_small)?.toInt(),
                position = DrawablePosition.END
            ),
            ::onSaveData
        )

    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditAvatatBottomSheetBinding =
        FragmentEditAvatatBottomSheetBinding.inflate(inflater, container, false)

    override fun bindViews() {
        super.bindViews()
    }

    override fun initObservers() {
        super.initObservers()
        observe(viewModel.avatarsLiveData, ::onAvatarsReceived)
    }

    override fun onLoadData() {
        super.onLoadData()
    }

    override fun onSaveData() {
        super.onSaveData()
        // Store changes

        if (isVisible && !isRemoving) dismiss()
    }

    private fun onAvatarsReceived(avatars: List<ProfileAvatar>) {
        binding.avatarsRecyclerView.adapter = AvatarsAdapter(
            items = avatars.toMutableList(),
            selected = profile?.avatar
        ).also { adapter = it }
    }
}
