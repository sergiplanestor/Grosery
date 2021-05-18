package com.revolhope.presentation.library.component.snackbar.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.Group
import com.revolhope.presentation.databinding.ComponentSnackbarViewBinding
import com.revolhope.presentation.library.component.snackbar.model.SnackBarModel
import com.revolhope.presentation.library.extensions.inflater

class SnackBarErrorView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SnackBarView<SnackBarModel.Error>(context, attrs, defStyleAttr) {

    private val binding = ComponentSnackbarViewBinding.inflate(
        context.inflater,
        this,
        true
    )

    override val contentGroup: Group? by lazy { binding.contentGroup }

    override fun bind(model: SnackBarModel.Error) {
        setAutoSizedText(view = binding.message, message = model.message)
    }
}
