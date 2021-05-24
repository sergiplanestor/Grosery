package com.revolhope.presentation.feature.dashboard.profile.edit.avatar.adapter

import android.view.ViewGroup
import com.revolhope.domain.feature.profile.model.ProfileAvatar
import com.revolhope.presentation.library.base.DiffUtilAdapter

class AvatarsAdapter(
    override val items: MutableList<ProfileAvatar>,
    var selected: ProfileAvatar?
): DiffUtilAdapter<ProfileAvatar, SelectableAvatarView>(items) {

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): SelectableAvatarView =
        SelectableAvatarView(context = parent.context)

    override fun onBindView(view: SelectableAvatarView, item: ProfileAvatar) {
        view.bind(item)
        view.isSelected = item.id == selected?.id
        view.setOnClickListener {
            selected = item
            notifyDataSetChanged()
        }
    }

    override fun areItemsTheSame(oldItem: ProfileAvatar, newItem: ProfileAvatar): Boolean =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: ProfileAvatar, newItem: ProfileAvatar): Boolean =
        oldItem.id == newItem.id
}
