package com.revolhope.presentation.library.component.loader

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.revolhope.presentation.databinding.ComponentLoaderViewBinding
import com.revolhope.presentation.library.extensions.inflater

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

    fun show() {
        binding.lottieAnimationView.playAnimation()
        isVisible = true
    }

    fun hide() {
        binding.lottieAnimationView.cancelAnimation()
        isVisible = false
    }
}