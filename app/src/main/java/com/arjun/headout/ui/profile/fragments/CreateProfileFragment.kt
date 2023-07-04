package com.arjun.headout.ui.profile.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.CurrencyHelper
import com.arjun.headout.data.local.preferences.LanguageHelper
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.City
import com.arjun.headout.data.model.Currency
import com.arjun.headout.data.model.HeadOuter
import com.arjun.headout.data.model.Language
import com.arjun.headout.databinding.FragmentCreateProfileBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.ui.profile.adapters.CityAdapter
import com.arjun.headout.ui.profile.adapters.CurrencyAdapter
import com.arjun.headout.ui.profile.adapters.LanguageAdapter
import com.arjun.headout.util.CompressImageUtil
import com.arjun.headout.util.ProgressLoadingHelper
import com.arjun.headout.util.ShimmerUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class CreateProfileFragment : Fragment(R.layout.fragment_create_profile) {

    private lateinit var binding: FragmentCreateProfileBinding

    private val viewModel: ProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private var userId = ""
    private var cities: List<City> = emptyList()

    private var languageCode = ""

    private var cityId = ""
    private var cityName = ""
    private var currencyCode = ""

    private lateinit var imageUri: Uri
    private var imageSelected = false

    private val getImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    imageUri = CompressImageUtil.compressImage(
                        context = requireContext(),
                        imageUri = result.data?.data!!,
                        quality = 75
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    makeToast(getString(R.string.error_occurred))
                    return@registerForActivityResult
                }
                binding.profileImageView.setImageURI(imageUri)
                imageSelected = true
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateProfileBinding.bind(view)

        loadUserInfo()
        setLanguageAdapter()
        setCurrencyAdapter()

        lifecycleScope.launch {
            cities = mainViewModel.getCitiesListRemote()
            setCityAdapter(cities)
        }

        binding.apply {
            changeImageButton.setOnClickListener {
                selectImage()
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
                        setText(city.name?.get("en") ?: "")
                        cityId = city.id
                        cityName = city.name?.get("en") ?: ""
                        setCityAdapter(cities)
                    }
            }
            currencyAutoCompleteTextView.apply {
                onItemClickListener =
                    AdapterView.OnItemClickListener { parent, _, position, _ ->
                        val currency = parent.getItemAtPosition(position) as Currency
                        setText("${currency.name} (${currency.symbol})")
                        currencyCode = currency.code.toString()
                        setCurrencyAdapter()
                    }
            }
            saveButton.setOnClickListener {
                val name = nameEditText.text.toString()
                if (inputValid(name, cityId, languageCode, currencyCode)) {
                    ProgressLoadingHelper.showProgressBar(
                        context = requireContext(),
                        getString(R.string.saving_info)
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        if (imageSelected) {
                            viewModel.uploadImage(imageUri)
                        }
                        saveToPreferences(
                            uid = userId,
                            name = name,
                            cityID = cityId,
                            cityName = cityName,
                            languageCode = languageCode,
                            currencyCode = currencyCode
                        )
                        viewModel.createProfile(
                            name = name,
                            cityId = cityId,
                            cityName = cityName,
                            languageCode = languageCode,
                            currencyCode = currencyCode
                        )
                        delay(1500) // waiting for preferences to be saved
                        withContext(Dispatchers.Main) {
                            ProgressLoadingHelper.dismissProgressBar()
                            findNavController().navigate(R.id.action_createProfileFragment_to_home_navigation)
                        }
                    }
                }
            }
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

    private fun setLanguageAdapter() {
        val languageOptions = LanguageHelper.languages
        val languageAdapter = LanguageAdapter(requireContext(), languageOptions)
        binding.languageAutoCompleteTextView.setAdapter(languageAdapter)
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

    private fun loadUserInfo() {
        lifecycleScope.launch {
            val profile = viewModel.getMyProfile()?.toObject(HeadOuter::class.java)
            profile?.apply {
                userId = uid
                ShimmerUtil.loadImageWithShimmer(
                    imageView = binding.profileImageView,
                    imageUrl = profileImageUrl,
                    placeholderDrawable = ResourcesCompat.getDrawable(
                        binding.profileImageView.context.resources,
                        R.drawable.profile_placeholder,
                        null
                    )
                )
                if (name != "" && name != "null") {
                    binding.nameEditText.setText(name)
                }
            }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getImageLauncher.launch(intent)
    }

    private fun inputValid(
        name: String,
        location: String,
        language: String,
        currency: String
    ): Boolean {
        when {
            name.isEmpty() -> {
                makeToast(getString(R.string.please_enter_your_name))
                return false
            }

            location.isEmpty() || location == getString(R.string.select_city) -> {
                makeToast(getString(R.string.select_preferred_location))
                return false
            }

            language.isEmpty() || language == getString(R.string.select_language) -> {
                makeToast(getString(R.string.select_preferred_language))
                return false
            }

            currency.isEmpty() || currency == getString(R.string.select_currency) -> {
                makeToast(getString(R.string.select_preferred_currency))
                return false
            }

            else -> {
                return true
            }
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

}