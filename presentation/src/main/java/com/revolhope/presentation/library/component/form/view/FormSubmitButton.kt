package com.revolhope.presentation.library.component.form.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.revolhope.presentation.databinding.ComponentFormSubmitButtonBinding
import com.revolhope.presentation.library.extensions.inflater
import com.revolhope.presentation.library.extensions.runOnUI

class FormSubmitButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_LOADING_TIME = 30000L // 30s
    }

    enum class State {
        IDLE,
        LOADING
    }

    private val binding = ComponentFormSubmitButtonBinding.inflate(context.inflater, this, true)

    var state: State = State.IDLE
        set(value) {
            changeState(value)
            field = value
        }

    var text: CharSequence = ""
        set(value) {
            if (state == State.IDLE) binding.formSubmitButton.text = value
            field = value
        }

    var onTimeoutReached: (() -> Unit)? = null

    var onSubmit: (() -> Unit)? = null

    init {
        setupListeners()
    }

    private fun changeState(state: State) {
        when (state) {
            State.IDLE -> {
                binding.formSubmitButton.text = text
            }
            State.LOADING -> {
                text = binding.formSubmitButton.text
                binding.formSubmitButton.text = ""
                with(binding.formButtonsLoader) {
                    playAnimation()
                    this.isVisible = true
                    runOnUI(MAX_LOADING_TIME) {
                        if (state == State.LOADING) {
                            this.isVisible = false
                            cancelAnimation()
                            binding.formSubmitButton.text = text
                            onTimeoutReached?.invoke()
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.formSubmitButton.setOnClickListener {
            onSubmit?.run {
                state = State.LOADING
                invoke()
            }
        }
    }
}