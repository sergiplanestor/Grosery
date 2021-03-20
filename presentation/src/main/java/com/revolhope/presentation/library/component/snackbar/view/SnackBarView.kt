package com.revolhope.presentation.library.component.snackbar.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.ContentViewCallback
import com.revolhope.presentation.library.component.snackbar.model.SnackBarModel

abstract class SnackBarView<T : SnackBarModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    abstract fun bind(model: T)

    override fun animateContentOut(delay: Int, duration: Int) {
        // Nothing to do here
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        // Nothing to do here
    }
}