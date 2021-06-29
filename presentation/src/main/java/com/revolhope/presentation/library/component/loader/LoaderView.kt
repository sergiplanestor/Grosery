package com.revolhope.presentation.library.component.loader

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.revolhope.presentation.databinding.ComponentLoaderViewBinding
import com.revolhope.presentation.library.extensions.inflater
import com.revolhope.presentation.library.extensions.isVisibleAnimated

typealias LoaderData = Pair<Boolean, LoadingMessageModel?>

class LoaderView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val binding = ComponentLoaderViewBinding.inflate(
        context.inflater,
        this,
        true
    )

    var onLoaderDataReceived: LoaderData? = null
        set(value) {
            value.also { field = value }?.let(::onDataChanged)
        }

    private fun onDataChanged(loaderData: LoaderData) {
        val info = loaderData.second?.messageOrEmpty(context)
        if (info.isNullOrBlank()) {
            binding.loaderInfoTextView.isVisible = false
        } else {
            binding.loaderInfoTextView.text = info
            binding.loaderInfoTextView.isVisibleAnimated = true
        }
        isVisible = loaderData.first
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == View.VISIBLE) {
            binding.lottieAnimationView.playAnimation()
        } else {
            binding.lottieAnimationView.cancelAnimation()
        }
    }
}
