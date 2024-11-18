package com.tghrsyahptra.dicodingeventapp.ui.activities

import android.Manifest
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.tghrsyahptra.dicodingeventapp.R
import com.tghrsyahptra.dicodingeventapp.databinding.ActivityMainBinding
import com.tghrsyahptra.dicodingeventapp.ui.dialogs.NetworkDialog
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.MainViewModel
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.ViewModelFactory
import com.tghrsyahptra.dicodingeventapp.utils.NetworkUtil
import com.tghrsyahptra.dicodingeventapp.utils.NetworkUtil.isNetworkAvailable
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkReceiver: NetworkUtil.NetworkChangeReceiver

    private var networkDialog: NetworkDialog? = null
    private var refreshListener: NetworkChangeListener? = null
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val message = if (isGranted) {
            "Notifications permission granted"
        } else {
            "Notifications permission rejected"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        if (Build.VERSION.SDK_INT >= 33) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Monitor theme settings and apply accordingly
        viewModel.getThemeSettings().observe(this) {
            setDarkMode(it)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeBottomNavigation()

        networkDialog = NetworkDialog(this)
        initializeNetworkReceiver()
    }

    private fun initializeBottomNavigation() {
        val bottomNav: BottomNavigationView = binding.bottomNav
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNav.setupWithNavController(navController)
    }

    private fun setDarkMode(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun initializeNetworkReceiver() {
        networkReceiver = NetworkUtil.NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                networkDialog?.showNoInternetDialog {
                    if (isNetworkAvailable(this)) {
                        networkDialog?.dismissDialog()
                        refreshListener?.onNetworkChanged()
                    } else {
                        Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                networkDialog?.dismissDialog()
                refreshListener?.onNetworkChanged()
            }
        }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        networkDialog?.dismissDialog()
    }

    interface NetworkChangeListener {
        fun onNetworkChanged()
    }
}