package com.arjun.headout.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.DEFAULT_PREFERENCE
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.databinding.ActivityMainBinding
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.util.network.NetworkConnected
import com.arjun.headout.util.ProgressLoadingHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zoho.livechat.android.ZohoLiveChat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val profileViewModel: ProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private var currentDestination: Int? = null

    private var noInternetDialog: AlertDialog? = null

    private var activityNavigationSet = false

    override fun onCreate(savedInstanceState: Bundle?) {

        lifecycleScope.launch {
            val languageCode =
                mainViewModel.readData(PreferencesHelper.SELECTED_LANGUAGE_CODE, DEFAULT_PREFERENCE)
            val locale = Locale(languageCode)
            setLanguage(locale)
        }

        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  Sending Network Status to SharedViewModel
        val networkConnected = NetworkConnected(this)
        networkConnected.observe(this) {
            mainViewModel.getNetwork(it)
        }

        ProgressLoadingHelper.showProgressBar(this, getString(R.string.loading))


        mainViewModel.isNetworkConnected.observe(this) {
            if (it) {
                ProgressLoadingHelper.dismissProgressBar()
                noInternetDialog?.dismiss()
                if (!activityNavigationSet) {
                    setActivityNavigation()
                    activityNavigationSet = true
                }
                binding.networkError.visibility = View.GONE
            } else {
                showNoInternetDialog()
                binding.networkError.visibility = View.VISIBLE
            }
        }

    }

    private fun setLanguage(locale: Locale) {

        val resources = this.resources
        val configuration = resources.configuration

        // Set the desired locale
        configuration.setLocale(locale)

        // Update the configuration
        this.createConfigurationContext(configuration)

        // Update the display metrics
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun setActivityNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.navigation_fragment) as NavHostFragment?
        val navController = navHost!!.navController
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.app_navigation)

        CoroutineScope(Dispatchers.Main).launch {
            if (profileViewModel.currentUser == null) {
                graph.setStartDestination(R.id.authenticationFragment)
            } else {
                if (profileViewModel.profileSet()) {
                    currentDestination = R.id.home_navigation
                    graph.setStartDestination(R.id.home_navigation)
                } else {
                    graph.setStartDestination(R.id.createProfileFragment)
                }
            }
            navController.graph = graph
        }

        setupBottomNavigationView(binding.bottomNavigationView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                R.id.homeFragment -> {
                    binding.bottomNavigationView.selectedItemId = R.id.home_navigation
                    binding.bottomNavBarDivider.visibility = View.VISIBLE
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }

                R.id.searchFragment -> {
                    binding.bottomNavigationView.selectedItemId = R.id.search_navigation
                    binding.bottomNavBarDivider.visibility = View.VISIBLE
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }

                R.id.communityFragment -> {
                    binding.bottomNavigationView.selectedItemId = R.id.community_navigation
                    binding.bottomNavBarDivider.visibility = View.VISIBLE
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }

                R.id.myProfileFragment -> {
                    binding.bottomNavigationView.selectedItemId = R.id.profile_navigation
                    binding.bottomNavBarDivider.visibility = View.VISIBLE
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }

                else -> {
                    binding.bottomNavBarDivider.visibility = View.GONE
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    private fun setupBottomNavigationView(
        bottomNavigationView: BottomNavigationView, navController: NavController
    ) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val destinationId = when (item.itemId) {
                R.id.home_navigation -> R.id.home_navigation
                R.id.search_navigation -> R.id.search_navigation
                R.id.community_navigation -> R.id.community_navigation
                R.id.support -> {
                    ZohoLiveChat.Chat.open(this)
                    null
                }

                R.id.profile_navigation -> R.id.profile_navigation
                else -> null
            }

            if (destinationId != null && currentDestination != destinationId) {
                currentDestination = destinationId
                navController.navigate(destinationId)
                true
            } else {
                false
            }
        }
    }

    private fun showNoInternetDialog() {
        val builder = MaterialAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.no_internet_connection))
            setMessage(getString(R.string.check_internet_connection))
            setCancelable(false)
            setPositiveButton(getString(R.string.okay)) { dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            }
        }
        noInternetDialog = builder.create()
        noInternetDialog?.show()
    }

}