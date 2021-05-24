package com.revolhope.presentation.library.component.profilecard

import android.content.Context
import android.util.AttributeSet
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ComponentProfileCardViewBinding
import com.revolhope.presentation.library.component.BaseView
import com.revolhope.presentation.library.extensions.avatar
import com.revolhope.presentation.library.extensions.getString
import com.revolhope.presentation.library.extensions.inflater

class ProfileCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseView<ProfileCardUiModel, ComponentProfileCardViewBinding>(context, attrs, defStyleAttr) {

    override val binding = ComponentProfileCardViewBinding.inflate(context.inflater, this, true)

    override fun bind(model: ProfileCardUiModel) {
        super.bind(model)
        binding.avatarImageView.avatar = model.avatar
        binding.usernameTextView.text = model.username
        binding.emailTextView.text = model.email
        model.numberOfLists.doOrGone(
            receiver = binding.numOfListsTextView,
            isGone = false,
            predicate = { it != null && it > 0},
            block = { text = getString(R.string.profile_num_of_lists,  it) }
        )
        binding.lastConnectionTextView.setTextOrGone(
            isGone = false,
            getString(R.string.profile_last_connection, model.lastConnectedOn.formatted)
        )
        binding.editButton.setOnClickListener { model.onEditClick.invoke() }
    }
}
