package com.arjun.headout.ui.profile.fragments

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.CurrencyHelper
import com.arjun.headout.data.local.preferences.DEFAULT_PREFERENCE
import com.arjun.headout.data.local.preferences.LanguageHelper
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.City
import com.arjun.headout.data.model.Currency
import com.arjun.headout.data.model.HeadOuter
import com.arjun.headout.data.model.Language
import com.arjun.headout.databinding.FragmentSettingsBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.ui.profile.adapters.CityAdapter
import com.arjun.headout.ui.profile.adapters.CurrencyAdapter
import com.arjun.headout.ui.profile.adapters.LanguageAdapter
import com.arjun.headout.util.ProgressLoadingHelper
import com.arjun.headout.util.cache.VideoStorageUtil
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zoho.livechat.android.ZohoLiveChat
import com.zoho.salesiqembed.ZohoSalesIQ
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private var cities: List<City> = emptyList()

    private var languageCode = DEFAULT_PREFERENCE
    private var cityId = DEFAULT_PREFERENCE
    private var cityName = DEFAULT_PREFERENCE
    private var currencyCode = DEFAULT_PREFERENCE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        loadUserInfo()

        lifecycleScope.launch {
            cities = mainViewModel.getCitiesListRemote()
            setCityAdapter(cities)
        }

        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            languageAutoCompleteTextView.apply {
                onItemClickListener =
                    AdapterView.OnItemClickListener { parent, _, position, _ ->
                        val language = parent.getItemAtPosition(position) as Language
                        setText(language.nativeName)
                        languageCode = language.code.toString()
                        val locale = Locale(languageCode)
                        updateLanguage(locale)
                        setLanguageAdapter()
                    }
            }
            cityAutoCompleteTextView.apply {
                onItemClickListener =
                    AdapterView.OnItemClickListener { parent, _, position, _ ->
                        val city = parent.getItemAtPosition(position) as City
                        cityId = city.id
                        cityName = city.name?.get("en") ?: ""
                        setText(cityName)
                        updateCity()
                        setCityAdapter(cities)
                    }
            }
            currencyAutoCompleteTextView.apply {
                onItemClickListener =
                    AdapterView.OnItemClickListener { parent, _, position, _ ->
                        val currency = parent.getItemAtPosition(position) as Currency
                        setText("${currency.name} (${currency.symbol})")
                        currencyCode = currency.code.toString()
                        updateCurrency()
                        setCurrencyAdapter()
                    }
            }
            aboutUsTextView.setOnClickListener {
                openCustomTab("https://www.headout.com/about-us/")
            }
            helpTextView.setOnClickListener {
                openCustomTab("https://www.headout.com/help/")
            }
            privacyPolicyTextView.setOnClickListener {
                openCustomTab("https://www.headout.com/privacy-policy/")
            }
            termsOfUsageTextView.setOnClickListener {
                openCustomTab("https://www.headout.com/terms-of-use/")
            }
            signOutButton.setOnClickListener {
                showSignOutDialog()
            }
        }
    }

    private fun loadUserInfo() {
        CoroutineScope(Dispatchers.Main).launch {
            val profile = profileViewModel.getMyProfile()?.toObject(HeadOuter::class.java)
            profile?.apply {
                binding.languageAutoCompleteTextView.gravity = Gravity.LEFT
                binding.languageAutoCompleteTextView.setText(languageCode?.let {
                    LanguageHelper.getLanguageByCode(it)?.name
                        ?: getString(R.string.select_language)
                })

                binding.cityAutoCompleteTextView.setText(cityName)

                binding.currencyAutoCompleteTextView.gravity = Gravity.LEFT
                binding.currencyAutoCompleteTextView.setText(currencyCode?.let {
                    CurrencyHelper.getCurrencyByCode(it)?.name
                        ?: getString(R.string.select_currency)
                })
                setLanguageAdapter()
                setCurrencyAdapter()
            }

        }
    }

    private fun updateCity() {

        mainViewModel.saveData(PreferencesHelper.LOCAL_DATA_SAVED, "false")
        mainViewModel.saveData(PreferencesHelper.LOCAL_VIDEO_SAVED, "false")
        mainViewModel.saveData(PreferencesHelper.SELECTED_CITY_ID, cityId)
        mainViewModel.saveData(PreferencesHelper.SELECTED_CITY_NAME, cityName)

        profileViewModel.updateCity(cityId = cityId, cityName = cityName)

        mainViewModel.fetchAndSaveDataFromRemote(
            context = requireActivity(),
            cityId = cityId
        )
    }

    private fun updateLanguage(locale: Locale) {
        mainViewModel.saveData(PreferencesHelper.SELECTED_LANGUAGE_CODE, languageCode)
        profileViewModel.updateLanguage(languageCode = languageCode)

        val resources = requireContext().resources
        val configuration = resources.configuration

        // Set the desired locale
        configuration.setLocale(locale)

        // Update the configuration
        requireContext().createConfigurationContext(configuration)

        // Update the display metrics
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Recreate the activity
        requireActivity().recreate()
    }

    private fun updateCurrency() {
        mainViewModel.saveData(PreferencesHelper.SELECTED_CURRENCY_CODE, currencyCode)
        profileViewModel.updateCurrency(currencyCode = currencyCode)
    }

    private fun setLanguageAdapter() {
        val languageOptions = LanguageHelper.languages
        val languageAdapter = LanguageAdapter(requireContext(), languageOptions)
        binding.languageAutoCompleteTextView.setAdapter(languageAdapter)
        binding.languageAutoCompleteTextView.dismissDropDown()
    }

    private fun setCityAdapter(cities: List<City>) {
        val cityAdapter = CityAdapter(requireContext(), cities)
        binding.cityAutoCompleteTextView.setAdapter(cityAdapter)
    }

    private fun setCurrencyAdapter() {
        val currencyOptions = CurrencyHelper.currencies
        val currencyAdapter = CurrencyAdapter(requireContext(), currencyOptions)
        binding.currencyAutoCompleteTextView.setAdapter(currencyAdapter)
    }

    private fun openCustomTab(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.intent.setPackage("com.android.chrome")
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }

    private fun showSignOutDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.sign_out))
            setMessage(getString(R.string.sign_out_confirmation))
            setCancelable(true)
            setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                ProgressLoadingHelper.showProgressBar(
                    context = requireContext(),
                    getString(R.string.signing_out)
                )

                AuthUI.getInstance().signOut(requireContext())
                ZohoSalesIQ.unregisterVisitor(context)
                ZohoLiveChat.unregisterVisitor(context)
                activity?.cacheDir?.deleteRecursively()
                mainViewModel.clearPreferences()
                VideoStorageUtil.deleteCityVideoUri(requireContext())

                ProgressLoadingHelper.dismissProgressBar()
                requireActivity().finishAffinity()
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        }.create()

        dialog.show()
    }

}