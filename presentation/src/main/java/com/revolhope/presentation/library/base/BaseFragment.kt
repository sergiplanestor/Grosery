package com.revolhope.presentation.library.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.revolhope.presentation.R
import com.revolhope.presentation.library.component.snackbar.SnackBar
import com.revolhope.presentation.library.component.snackbar.model.SnackBarModel

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    abstract fun inflateView(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflateView(inflater, container).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        initObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    open fun bindViews() {
        // Nothing to do here
    }

    open fun initObservers() {
        // Nothing to do here
    }

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
}
