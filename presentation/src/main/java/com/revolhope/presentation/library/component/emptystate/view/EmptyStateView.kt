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

    override val binding = ComponentEmptyStateViewBinding.inflate(context.inflater, this, true)

    override fun bind(model: EmptyStateUiModel) {
        super.bind(model)
        binding.title.text = model.titleOrDefault(context)
        binding.message.setTextOrGone(value = model.message)
        model.actionName.doOrGone(receiver = binding.action) {
            text = it
            setOnClickListener { model.action?.invoke() }
            isVisible = true
        }
    }
}
