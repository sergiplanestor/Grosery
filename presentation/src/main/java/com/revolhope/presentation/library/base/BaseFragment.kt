package com.revolhope.presentation.library.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.revolhope.domain.common.extensions.verbose
import com.revolhope.presentation.R
import com.revolhope.presentation.library.component.snackbar.SnackBar
import com.revolhope.presentation.library.component.snackbar.model.SnackBarModel
import com.revolhope.presentation.utils.LifecycleEvent
import com.revolhope.presentation.utils.logLifecycle

abstract class BaseFragment : Fragment() {

    protected val baseActivity: BaseActivity? get() = (activity as? BaseActivity)
    protected lateinit var rootView: View

    abstract fun onCreateBinding(inflater: LayoutInflater, container: ViewGroup?): View

    abstract fun onDestroyBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logLifecycle(LifecycleEvent.ON_CREATE)
        return onCreateBinding(inflater, container).also { rootView = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifecycle(LifecycleEvent.ON_VIEW_CREATED)
        bindViews()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        logLifecycle(LifecycleEvent.ON_START)
        onLoadData()
    }

    override fun onResume() {
        super.onResume()
        logLifecycle(LifecycleEvent.ON_RESUME)
    }

    override fun onPause() {
        super.onPause()
        logLifecycle(LifecycleEvent.ON_PAUSE)
        onSaveData()
    }

    override fun onStop() {
        super.onStop()
        logLifecycle(LifecycleEvent.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifecycle(LifecycleEvent.ON_DESTROY)
        onDestroyBinding()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logLifecycle(LifecycleEvent.ON_ATTACH)
    }

    override fun onDetach() {
        super.onDetach()
        logLifecycle(LifecycleEvent.ON_ATTACH)
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
