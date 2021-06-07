package com.revolhope.presentation.feature.dashboard.profile.edit.avatar.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.revolhope.presentation.library.extensions.dp

class AvatarItemDecorator : RecyclerView.ItemDecoration() {

    companion object {
        private const val MARGIN_INNER = 12
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val numOfItems = parent.adapter?.itemCount ?: 0
        when (parent.getChildAdapterPosition(view)) {
            numOfItems - 1 -> {
                view.setPadding(MARGIN_INNER, 0, MARGIN_INNER.dp, 0)
            }
            else -> {
                view.setPadding(MARGIN_INNER.dp, 0, 0, 0)
            }
        }
    }

}
