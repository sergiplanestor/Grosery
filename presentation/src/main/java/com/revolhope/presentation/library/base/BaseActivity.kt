package com.revolhope.presentation.library.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.revolhope.presentation.R
import com.revolhope.presentation.library.component.loader.LoaderView
import com.revolhope.presentation.library.component.snackbar.SnackBar
import com.revolhope.presentation.library.component.snackbar.model.SnackBarModel

abstract class BaseActivity : AppCompatActivity() {

    protected enum class NavTransition {
        LATERAL,
        MODAL
    }

    companion object {
        const val EXTRA_NAVIGATION_TRANSITION = "nav.transition"
    }

    private lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(inflateView().also { root = it })
        bindViews()
        initObservers()
    }

    abstract fun inflateView(): View

    open fun bindViews() {
        // Nothing to do here
    }

    open fun initObservers() {
        // Nothing to do here
    }

    protected fun onErrorReceived(error: String) {
        SnackBar.show(root, SnackBarModel.Error(error))
    }

    /*protected fun onLoaderVisibilityChanges(show: Boolean) {
        findViewById<LoaderView?>(R.id.loader_view)?.let {
            if (show) it.show() else it.hide()
        }
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        overrideTransition()
    }

    override fun startActivity(intent: Intent?) {
        val anim = getNavAnimations(intent)
        super.startActivity(intent)
        overridePendingTransition(anim.first, anim.second)
    }

    /**
     * Private util method to obtain animation to perform when changing between activities.
     * @param intent [Intent] object to obtain extra param and know which [NavTransition] should be executed.
     * @param isStart [Boolean] indicates if this animation is from starting new activity or if it is from
     * going back from.
     *
     * @return [Pair] object containing animations to perform. [Pair.first] animation resource is for current activity,
     * [Pair.second] animation resource is for incoming activity.
     */
    private fun getNavAnimations(intent: Intent?, isStart: Boolean = true): Pair<Int, Int> {
        val bundle = intent?.extras
        return when (bundle?.getSerializable(EXTRA_NAVIGATION_TRANSITION) as NavTransition?) {
            NavTransition.LATERAL ->
                if (isStart) {
                    bundle?.putSerializable(EXTRA_NAVIGATION_TRANSITION, NavTransition.LATERAL)
                    Pair(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    Pair(R.anim.slide_in_left, R.anim.slide_out_right)
                }
            NavTransition.MODAL ->
                if (isStart) {
                    bundle?.putSerializable(EXTRA_NAVIGATION_TRANSITION, NavTransition.MODAL)
                    Pair(R.anim.slide_down, R.anim.hold)
                } else {
                    Pair(R.anim.hold, R.anim.slide_up)
                }
            else -> Pair(0, 0)
        }
    }

    /**
     * Private util method to override transitions between activities.
     */
    private fun overrideTransition() {
        val anim = getNavAnimations(intent, isStart = false)
        overridePendingTransition(anim.first, anim.second)
    }
}