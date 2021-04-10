package com.revolhope.presentation.feature.dashboard

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import com.revolhope.presentation.databinding.ActivityDashboardBinding
import com.revolhope.presentation.library.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding
    //private val viewModel: DashboardViewModel by viewModels()

    companion object {
        fun start(baseActivity: BaseActivity) {
            baseActivity.startActivity(
                Intent(baseActivity, DashboardActivity::class.java).apply {
                    putExtras(
                        bundleOf(
                            EXTRA_NAVIGATION_TRANSITION to NavTransition.MODAL
                        )
                    )
                }
            )
        }
    }

    override fun inflateView(): View =
        ActivityDashboardBinding.inflate(layoutInflater).let {
            binding = it
            it.root
        }
}
