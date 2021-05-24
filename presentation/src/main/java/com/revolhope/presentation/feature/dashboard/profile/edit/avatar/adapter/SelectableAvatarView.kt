package com.revolhope.presentation.feature.dashboard.profile.edit.avatar.adapter

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import com.revolhope.domain.feature.profile.model.ProfileAvatar
import com.revolhope.presentation.databinding.ComponentSelectableAvatarViewBinding
import com.revolhope.presentation.library.component.BaseView
import com.revolhope.presentation.library.extensions.avatar
import com.revolhope.presentation.library.extensions.inflater

class SelectableAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseView<ProfileAvatar, ComponentSelectableAvatarViewBinding>(context, attrs, defStyleAttr) {

    override val binding: ComponentSelectableAvatarViewBinding
        get() = ComponentSelectableAvatarViewBinding.inflate(context.inflater, this, true)

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        binding.avatarSelectableIndicator.isVisible = selected
    }

    override fun isSelected(): Boolean {
        return super.isSelected() || binding.avatarSelectableIndicator.isVisible
    }

    override fun bind(model: ProfileAvatar) {
        super.bind(model)
        binding.avatarImageView.avatar = model
    }
}
