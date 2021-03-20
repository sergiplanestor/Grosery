package com.revolhope.presentation.library.component.snackbar.view

import android.content.Context
import android.util.AttributeSet
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

    override fun bind(model: SnackBarModel.Error) {
        binding.message.text = model.message
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        binding.message.alpha = 1f
        binding.message.animate().alpha(0f).setDuration(duration.toLong())
            .setStartDelay(delay.toLong())
            .start()
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        binding.message.alpha = 0f
        binding.message.animate().alpha(1f).setDuration(duration.toLong())
            .setStartDelay(delay.toLong())
            .start()
    }
}