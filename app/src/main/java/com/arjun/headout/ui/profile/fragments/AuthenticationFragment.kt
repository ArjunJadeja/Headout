package com.arjun.headout.ui.profile.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.HeadOuter
import com.arjun.headout.databinding.FragmentAuthenticationBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.util.ProgressLoadingHelper
import com.arjun.headout.util.cache.VideoStorageUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class AuthenticationFragment : Fragment(R.layout.fragment_authentication) {

    private lateinit var binding: FragmentAuthenticationBinding

    private val profileViewModel: ProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAuthenticationBinding.bind(view)

        VideoStorageUtil.deleteCityVideoUri(requireContext())
        mainViewModel.clearPreferences()

        // Authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.app_launcher)
            .setTheme(R.style.Theme_Headout)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        try {
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val user = FirebaseAuth.getInstance().currentUser
                    ProgressLoadingHelper.showProgressBar(
                        context = requireContext(),
                        getString(R.string.getting_info)
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        if (profileViewModel.isUserRegistered(user!!.uid)) {
                            saveProfileInfoAndNavigateToHome(user.uid)
                        } else {
                            profileViewModel.registerUser(
                                user.uid,
                                user.displayName.toString(),
                                user.email.toString(),
                                user.photoUrl.toString()
                            )
                            withContext(Dispatchers.Main) {
                                ProgressLoadingHelper.dismissProgressBar()
                                findNavController().navigate(R.id.action_authenticationFragment_to_createProfileFragment)
                            }
                        }
                    }
                }

                Activity.RESULT_CANCELED -> {
                    requireActivity().finishAffinity()
                }

                else -> {
                    Toast.makeText(context, getString(R.string.signin_error), Toast.LENGTH_SHORT)
                        .show()
                    Log.e("SignIn Error", result.resultCode.toString())
                }
            }
        } catch (e: Exception) {
            Log.e("SignIn Error", e.toString())
            Toast.makeText(context, getString(R.string.signin_error), Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun saveProfileInfoAndNavigateToHome(uid: String) {
        val profile = profileViewModel.getMyProfile()?.toObject(HeadOuter::class.java)
        profile?.apply {
            saveToPreferences(
                uid = uid,
                name = name.toString(),
                cityID = cityId.toString(),
                cityName = cityName.toString(),
                languageCode = languageCode.toString(),
                currencyCode = currencyCode.toString()
            )
        }
        profile?.let {
            it.languageCode?.let { languageCode ->
                updateLanguage(Locale(languageCode))
            }
        }
        delay(1500) // waiting for preferences to be saved
        withContext(Dispatchers.Main) {
            ProgressLoadingHelper.dismissProgressBar()
            findNavController().navigate(R.id.action_authenticationFragment_to_home_navigation)
        }
    }

    private fun updateLanguage(locale: Locale) {

        val resources = requireContext().resources
        val configuration = resources.configuration

        // Set the desired locale
        configuration.setLocale(locale)

        // Update the configuration
        requireContext().createConfigurationContext(configuration)

        // Update the display metrics
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun saveToPreferences(
        uid: String,
        name: String,
        cityID: String,
        cityName: String,
        languageCode: String,
        currencyCode: String
    ) {
        mainViewModel.saveData(PreferencesHelper.USER_ID, uid)
        mainViewModel.saveData(PreferencesHelper.USER_NAME, name)
        mainViewModel.saveData(PreferencesHelper.SELECTED_CITY_ID, cityID)
        mainViewModel.saveData(PreferencesHelper.SELECTED_CITY_NAME, cityName)
        mainViewModel.saveData(PreferencesHelper.SELECTED_LANGUAGE_CODE, languageCode)
        mainViewModel.saveData(PreferencesHelper.SELECTED_CURRENCY_CODE, currencyCode)
    }

}