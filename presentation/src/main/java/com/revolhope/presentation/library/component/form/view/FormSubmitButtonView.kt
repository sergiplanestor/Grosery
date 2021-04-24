package com.revolhope.presentation.library.component.form.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.revolhope.presentation.databinding.ComponentFormSubmitButtonViewBinding
import com.revolhope.presentation.library.extensions.inflater

class FormSubmitButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_LOADING_TIME = 30000L // 30s
    }

    enum class State(val id: Int) {
        IDLE(1),
        LOADING(2)
    }

    private val binding = ComponentFormSubmitButtonViewBinding.inflate(context.inflater, this, true)

    private val timeoutHandler = Handler(Looper.getMainLooper(), ::onTimeoutReached)

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
                binding.progressBar.isVisible = false
                binding.formSubmitButton.text = text
                setupListeners()
                removeTimeoutActions()
            }
            State.LOADING -> {
                text = binding.formSubmitButton.text.toString()
                binding.formSubmitButton.text = ""
                binding.formSubmitButton.setOnClickListener(null)
                binding.progressBar.isVisible = true
                timeoutHandler.sendEmptyMessageDelayed(State.LOADING.id, MAX_LOADING_TIME)
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

    private fun onTimeoutReached(message: Message): Boolean =
        true.also {
            if (message.what == State.LOADING.id) {
                onTimeoutReached?.invoke()
                changeState(State.IDLE)
            }
        }

    private fun removeTimeoutActions() {
        if (timeoutHandler.hasMessages(State.LOADING.id)) {
            timeoutHandler.removeMessages(State.LOADING.id)
        }
    }
}
