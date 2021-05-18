package com.revolhope.presentation.library.component.snackbar.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.doOnPreDraw
import androidx.core.widget.TextViewCompat
import com.google.android.material.snackbar.ContentViewCallback
import com.revolhope.presentation.R
import com.revolhope.presentation.library.component.snackbar.model.SnackBarModel
import com.revolhope.presentation.library.extensions.dimensionOf

abstract class SnackBarView<T : SnackBarModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    protected companion object {
        private const val DEFAULT_MESSAGE_MAX_LINES = 3
        private const val GRANULARITY = 1
    }

    // In case of being overridden, the getter method should be implemented by lazy to ensure ViewBinding
    // is already created.
    protected open val contentGroup: Group? = null

    open val minSize by lazy { dimensionOf(R.dimen.Text_Body2).toInt() }
    open val maxSize by lazy { dimensionOf(R.dimen.Text_Subtitle1).toInt() }

    abstract fun bind(model: T)

    protected fun setAutoSizedText(view: TextView, message: String) {
        with(view) {
            text = message
            doOnPreDraw {
                if (view.lineCount > DEFAULT_MESSAGE_MAX_LINES) {
                    TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                        this,
                        minSize,
                        maxSize,
                        GRANULARITY,
                        TypedValue.COMPLEX_UNIT_PX
                    )
                }
                view.maxLines = DEFAULT_MESSAGE_MAX_LINES
            }
        }
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        animateContent(delay.toLong(), duration.toLong(), isShowing = false)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        animateContent(delay.toLong(), duration.toLong(), isShowing = true)
    }

    protected open fun animateContent(delay: Long, duration: Long, isShowing: Boolean) {
        contentGroup?.run {
            alpha = if (isShowing) 0f else 1f
            animate().alpha(if (isShowing) 1f else 0f).apply {
                this.duration = duration
                startDelay = delay
                start()
            }
        }
    }
}
