package com.revolhope.presentation.feature.dashboard.profile.edit.avatar.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.revolhope.domain.feature.profile.model.ProfileAvatar
import com.revolhope.presentation.R
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.extensions.avatar
import com.revolhope.presentation.library.extensions.inflater
import com.revolhope.presentation.library.extensions.isVisibleAnimated

class AvatarsAdapter(
    val items: List<ProfileAvatar>,
    var selected: ProfileAvatar?,
    val baseActivity: BaseActivity?
) : RecyclerView.Adapter<AvatarsAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(
            parent.context.inflater.inflate(
                R.layout.holder_selectable_avatar_view,
                parent,
                false
            ),
            baseActivity
        )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position]) {
            if (selected?.id != items[position].id) {
                selected = items[position]
                notifyDataSetChanged()
            }
        }
        holder.isSelected = items[position].id == selected?.id
    }

    override fun getItemCount(): Int = items.count()

    class Holder(val view: View, val baseActivity: BaseActivity?) : RecyclerView.ViewHolder(view) {
        private val avatarSelectableIndicator: View by lazy { view.findViewById(R.id.avatar_selectable_indicator) }
        private val avatarImageView: ImageView by lazy { view.findViewById(R.id.avatar_image_view) }
        var isSelected: Boolean = false
            set(value) {
                field = value
                avatarSelectableIndicator.isVisibleAnimated = value
            }

        fun bind(model: ProfileAvatar, onClick: () -> Unit) {
            avatarImageView.avatar = model
            avatarSelectableIndicator.isVisibleAnimated = false
            view.setOnClickListener { onClick.invoke() }
            view.setOnLongClickListener {
                Toast.makeText(view.context, "T_Show avatar zoomed in", Toast.LENGTH_LONG).show()
                true
            }
        }
    }
}
