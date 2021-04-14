package com.revolhope.presentation.feature.dashboard

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.revolhope.presentation.R
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

    override fun bindViews() {
        super.bindViews()
        //val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(
            navController = navController,
            configuration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_dashboard,
                    R.id.navigation_notifications
                )
            )
        )
        binding.navView.setupWithNavController(navController)
    }
}
