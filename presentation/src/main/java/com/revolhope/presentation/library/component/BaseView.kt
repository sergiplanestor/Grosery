package com.revolhope.presentation.library.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding

abstract class BaseView<T, V : ViewBinding> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    abstract fun bind(model: T)

    protected fun View.gone() {
        isVisible = false
    }
}
