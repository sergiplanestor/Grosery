package com.revolhope.presentation.library.component.snackbar

import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.revolhope.domain.common.extensions.delay
import com.revolhope.presentation.library.component.snackbar.model.SnackBarModel
import com.revolhope.presentation.library.component.snackbar.view.SnackBarErrorView
import com.revolhope.presentation.library.component.snackbar.view.SnackBarView
import com.revolhope.presentation.library.extensions.dp
import com.revolhope.presentation.library.extensions.findSuitableParent

class SnackBar(
    parent: ViewGroup,
    content: SnackBarView<*>
) : BaseTransientBottomBar<SnackBar>(parent, content, content) {

    private var onDismiss: (() -> Unit)? = null

    init {
        getView().apply {
            setBackgroundColor(context.getColor(android.R.color.transparent))
            setPadding(0, 0, 0, PADDING_BOTTOM_DP.dp)
        }
    }

    override fun dismiss() {
        super.dismiss()
        onDismiss?.invoke()
    }

    companion object {

        private const val PADDING_BOTTOM_DP = 20
        private const val DURATION = 5000L // 5s

        fun show(view: View?, model: SnackBarModel) {

            val viewGroup = view.findSuitableParent() ?: return

            val content: SnackBarView<*> = when (model) {
                is SnackBarModel.Success -> {
                    // TODO: Change to positive view!
                    SnackBarErrorView(viewGroup.context)
                }
                is SnackBarModel.Error -> {
                    SnackBarErrorView(viewGroup.context).apply { bind(model) }
                }
            }

            SnackBar(
                viewGroup,
                content
            ).apply {
                this.duration = LENGTH_INDEFINITE
                this.onDismiss = model.onDismiss
                content.setOnClickListener {
                    model.onClick?.invoke()
                    dismiss()
                }
            }.also { delay(duration = DURATION, action = it::dismiss) }.show()
        }
    }
}
