package com.revolhope.presentation.library.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding

abstract class BaseView<M : Any, V : ViewBinding> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    protected abstract val binding: V
    protected lateinit var model: M

    @CallSuper
    open fun bind(model: M) {
        this.model = model
    }

    protected fun View.invisible() {
        isInvisible = true
    }

    protected fun View.gone() {
        isVisible = false
    }

    protected fun TextView.setTextOrGone(isGone: Boolean = true, value: CharSequence?) {
        value.doOrGone(
            receiver = this,
            isGone = isGone
        ) { text = it }
    }

    protected inline fun <T : Any, V : View, R> T?.doOrGone(
        receiver: V,
        isGone: Boolean = true,
        crossinline predicate: (T?) -> Boolean = {
            it != null || (it as? CharSequence).isNullOrBlank().not()
        },
        crossinline block: V.(T) -> R
    ): R? =
        takeIf(predicate)?.let {
            receiver.isVisible = true
            block.invoke(receiver, it)
        } ?: if (isGone) {
                receiver.isVisible = false
            } else {
            receiver.isInvisible = true
            }.let { null }
}
