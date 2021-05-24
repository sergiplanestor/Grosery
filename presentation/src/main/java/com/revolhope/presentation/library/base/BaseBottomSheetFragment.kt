package com.revolhope.presentation.library.base

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.FragmentBaseBottomSheetBinding
import com.revolhope.presentation.library.component.snackbar.SnackBar
import com.revolhope.presentation.library.component.snackbar.model.SnackBarModel
import com.revolhope.presentation.library.extensions.ClickableViewUiModel
import com.revolhope.presentation.library.extensions.bindClickableModel
import com.revolhope.presentation.library.extensions.inflater

// TODO: Apply loader
abstract class BaseBottomSheetFragment<T : ViewBinding> : BottomSheetDialogFragment() {

    private var _innerBinding: FragmentBaseBottomSheetBinding? = null
    private val innerBinding get() = _innerBinding!!

    private var _binding: T? = null
    protected val binding get() = _binding!!

    abstract val title: CharSequence
    abstract val doneButtonUiModel: ClickableViewUiModel

    abstract fun inflateView(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        FragmentBaseBottomSheetBinding.inflate(
            inflater,
            container,
            false
        ).also { _innerBinding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        onLoadData()
    }

    override fun onPause() {
        super.onPause()
        onSaveData()
    }

    open fun bindViews() {
        context?.inflater?.let { inflater ->
            innerBinding.dialogContent.addView(
                inflateView(inflater, innerBinding.dialogContent).also { _binding = it }.root
            )
        } ?: dismiss().also { return }
        innerBinding.dialogTitleTextView.text = title
        innerBinding.dialogDoneButton.bindClickableModel(doneButtonUiModel)
    }

    open fun initObservers() {
        // Nothing to do here
    }

    open fun onLoadData() {
        // Nothing to do here
    }

    open fun onSaveData() {
        // Nothing to do here
    }

    override fun onDestroy() {
        super.onDestroy()
        _innerBinding = null
        _binding = null
    }

    protected var args: List<Pair<String, Parcelable>> = emptyList()
        set(value) {
            field = value
            applyArguments(*value.toTypedArray())
        }

    fun <F : BaseBottomSheetFragment<*>, A : Parcelable> F.applyArguments(
        vararg pairs: Pair<String, A>
    ): F = apply { arguments = bundleOf(*pairs) }

    protected open fun onErrorReceived(
        error: String? = null,
        onClick: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ) {
        SnackBar.show(
            view = binding.root,
            model = SnackBarModel.Error(
                message = error ?: getString(R.string.error_default),
                onClick = onClick,
                onDismiss = onDismiss
            )
        )
    }

    fun show(manager: FragmentManager) {
        super.show(manager, this::class.java.simpleName)
    }
}
