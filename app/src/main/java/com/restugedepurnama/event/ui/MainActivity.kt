package com.restugedepurnama.event.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.restugedepurnama.event.R
import com.restugedepurnama.event.databinding.ActivityMainBinding
import com.restugedepurnama.event.settings.DailyReminderWorker
import com.restugedepurnama.event.settings.SettingPreferences
import com.restugedepurnama.event.settings.dataStore
import com.restugedepurnama.event.ui.viewModel.MainViewModel
import com.restugedepurnama.event.ui.viewModel.ViewModelFactory
import java.util.concurrent.TimeUnit
import com.restugedepurnama.event.ui.fragment.HomeFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext, SettingPreferences.getInstance(applicationContext.dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_event)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_finished_event,
                R.id.navigation_upcoming,
                R.id.navigation_favorite,
                R.id.navigation_setting
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        themeSwitch()
        reminderSwitch()

        if (savedInstanceState == null) {
            intent?.let {
                val eventId = it.getStringExtra("eventId")
                if (eventId != null) {
                    val action = HomeFragmentDirections.actionNavigationHomeToDetailFragment(eventId)
                    navController.navigate(action)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_finished_event, R.id.navigation_upcoming, R.id.navigation_favorite, R.id.navigation_setting -> showBottomNavigation()
                else -> hideBottomNavigation()
            }
        }
    }

    fun hideBottomNavigation() {
        binding.navView.apply {
            if (visibility == BottomNavigationView.VISIBLE) {
                visibility = BottomNavigationView.GONE
            }
        }
    }

    private fun showBottomNavigation() {
        binding.navView.apply {
            if (visibility == BottomNavigationView.GONE) {
                visibility = BottomNavigationView.VISIBLE
            }
        }
    }

    private fun themeSwitch() {
        viewModel.getThemeSetting().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                setDefaultNightMode(MODE_NIGHT_YES)
            } else {
                setDefaultNightMode(MODE_NIGHT_NO)
            }
        }
    }

    private fun reminderSwitch() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}