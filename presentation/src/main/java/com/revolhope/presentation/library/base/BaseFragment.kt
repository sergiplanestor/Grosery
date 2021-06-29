package com.revolhope.presentation.library.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.revolhope.domain.common.extensions.verbose
import com.revolhope.presentation.R
import com.revolhope.presentation.library.component.snackbar.SnackBar
import com.revolhope.presentation.library.component.snackbar.model.SnackBarModel

abstract class BaseFragment : Fragment() {

    protected val baseActivity: BaseActivity? get() = (activity as? BaseActivity)
    protected lateinit var rootView: View

    abstract fun onCreateBinding(inflater: LayoutInflater, container: ViewGroup?): View

    abstract fun onDestroyBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = onCreateBinding(inflater, container).also { rootView = it }

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

    override fun onDestroy() {
        super.onDestroy()
        onDestroyBinding()
    }

    open fun bindViews() {
        // Nothing to do here
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

    protected open fun onErrorReceived(
        error: String? = null,
        onClick: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ) {
        SnackBar.show(
            view = rootView,
            model = SnackBarModel.Error(
                message = error ?: getString(R.string.error_default),
                onClick = onClick,
                onDismiss = onDismiss
            )
        )
        if (error != null) {
            // TODO: Remove
            verbose(error)
        }
    }
}
