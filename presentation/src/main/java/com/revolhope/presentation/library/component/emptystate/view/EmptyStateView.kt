package com.revolhope.presentation.library.component.emptystate.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import com.revolhope.presentation.databinding.ComponentEmptyStateViewBinding
import com.revolhope.presentation.library.component.BaseView
import com.revolhope.presentation.library.component.emptystate.model.EmptyStateUiModel
import com.revolhope.presentation.library.extensions.inflater

class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseView<EmptyStateUiModel, ComponentEmptyStateViewBinding>(context, attrs, defStyleAttr) {

    val binding  = ComponentEmptyStateViewBinding.inflate(context.inflater, this, true)

    override fun bind(model: EmptyStateUiModel) {
        binding.title.text = model.titleOrDefault(context)
        model.message?.let { binding.message.text = it } ?: binding.message.gone()
        model.actionName?.let {
            with(binding.action) {
                text = it
                setOnClickListener { model.action?.invoke() }
                isVisible = true
            }
        } ?: binding.action.gone()
    }
}
