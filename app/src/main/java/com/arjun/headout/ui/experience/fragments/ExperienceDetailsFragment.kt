package com.arjun.headout.ui.experience.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.CurrencyHelper
import com.arjun.headout.data.local.preferences.DEFAULT_PREFERENCE
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.Experience
import com.arjun.headout.data.model.ExperienceDetail
import com.arjun.headout.databinding.FragmentExperienceDetailsBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.experience.adapters.ExperienceImagePagerAdapter
import com.arjun.headout.ui.profile.ProfileViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExperienceDetailsFragment : Fragment(R.layout.fragment_experience_details) {

    private lateinit var binding: FragmentExperienceDetailsBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var experience: Experience? = null
    private var experienceDetail: ExperienceDetail? = null

    private var languageCode = DEFAULT_PREFERENCE
    private var currencyCode = DEFAULT_PREFERENCE

    private var autoSlideJob: Job? = null

    private var ticketPrice = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExperienceDetailsBinding.bind(view)

        mainViewModel.selectedExperience.observe(viewLifecycleOwner) {
            it?.let { experience ->
                this.experience = experience
                lifecycleScope.launch {
                    loadImages(experience.mediaUrls?.images?.filterNotNull().orEmpty())
                    languageCode =
                        mainViewModel.readData(
                            PreferencesHelper.SELECTED_LANGUAGE_CODE,
                            DEFAULT_PREFERENCE
                        )
                    currencyCode =
                        mainViewModel.readData(
                            PreferencesHelper.SELECTED_CURRENCY_CODE,
                            DEFAULT_PREFERENCE
                        )
                    binding.experienceTitle.text =
                        experience.title?.get(languageCode) ?: "Not Found"
                    binding.experienceDescription.text =
                        (experience.description?.get(languageCode) ?: "Not Found").toString()
                    binding.experienceHighlights.text =
                        (experience.highlights?.get(languageCode) ?: "Not Found").toString()
                }
            }
        }
        lifecycleScope.launch {
            experienceDetail = mainViewModel.getExperienceDetails()
            if (experienceDetail != null) {
                val pricingList = experienceDetail!!.pricing
                if (pricingList != null) {
                    val pricing = pricingList.find { it.currency == currencyCode }
                    if (pricing != null) {
                        ticketPrice = pricing.price ?: 100.0
                    }
                }
                val currencySymbol = CurrencyHelper.getCurrencyByCode(currencyCode)?.symbol
                binding.apply {
                    experiencePriceMRP.text = "$currencySymbol $ticketPrice"
                    experienceYourExperience.text =
                        (experienceDetail!!.yourExperience?.get(languageCode)
                            ?: getString(R.string.not_found)).toString()
                    experienceKnowBeforeYouGo.text =
                        (experienceDetail!!.knowBeforeYouGo?.get(languageCode)
                            ?: getString(R.string.not_found)).toString()
                    experienceInclusions.text =
                        (experienceDetail!!.inclusions?.get(languageCode) ?: getString(R.string.not_found)).toString()
                    experienceExclusions.text =
                        (experienceDetail!!.exclusions?.get(languageCode) ?: getString(R.string.not_found)).toString()
                    experienceCancellationPolicy.text =
                        (experienceDetail!!.cancellationPolicy?.get(languageCode)
                            ?: getString(R.string.not_found)).toString()
                }
            }
        }
        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            saveExperienceButton.setOnClickListener {
                profileViewModel.saveExperience(experience?.id.toString())
                makeToast(toast = getString(R.string.experience_saved))
            }
            bookNowButton.setOnClickListener {
                findNavController().navigate(R.id.action_experienceDetailsFragment_to_experienceBookingFragment)
            }
        }
    }

    private fun makeToast(toast: String) {
        Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT).show()
    }

    private fun loadImages(images: List<String>) {
        val experienceImagePagerAdapter = ExperienceImagePagerAdapter(
            childFragmentManager,
            lifecycle,
            images
        )
        binding.imageViewPager.adapter = experienceImagePagerAdapter
        startAutoSlide()
    }

    private fun startAutoSlide() {
        autoSlideJob = lifecycleScope.launch {
            while (true) {
                delay(3000L)
                val currentItem = binding.imageViewPager.currentItem
                val nextItem = (currentItem + 1) % (binding.imageViewPager.adapter?.itemCount ?: 1)
                binding.imageViewPager.setCurrentItem(nextItem, true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoSlideJob?.cancel()
    }

}