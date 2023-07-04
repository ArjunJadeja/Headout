package com.arjun.headout.ui.profile.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arjun.headout.R
import com.arjun.headout.data.model.HeadOuter
import com.arjun.headout.databinding.FragmentMyProfileBinding
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.ui.profile.adapters.ExperiencesViewPagerAdapter
import com.arjun.headout.util.ShimmerUtil
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class MyProfileFragment : Fragment(R.layout.fragment_my_profile) {

    private lateinit var binding: FragmentMyProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyProfileBinding.bind(view)

        val viewPagerAdapter = ExperiencesViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        val typeface = ResourcesCompat.getFont(requireContext(), R.font.rubik_medium)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // creating a new TextView
            val textView = TextView(context)

            // setting the text, color, and typeface
            textView.text = when (position) {
                0 -> getString(R.string.my_bookings)
                1 -> getString(R.string.saved_experiences)
                else -> throw IllegalStateException("Invalid position: $position")
            }
            textView.gravity = Gravity.CENTER
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.typeface = typeface

            tab.customView = textView
        }.attach()


        lifecycleScope.launch {
            val profile = viewModel.getMyProfile()?.toObject(HeadOuter::class.java)
            profile?.apply {
                ShimmerUtil.loadImageWithShimmer(
                    imageView = binding.profileImageView,
                    imageUrl = profileImageUrl,
                    placeholderDrawable = ResourcesCompat.getDrawable(
                        binding.profileImageView.context.resources,
                        R.drawable.profile_placeholder,
                        null
                    )
                )
                binding.nameTextView.text = name
                if (socialNetworkLink?.isNotEmpty() == true) {
                    binding.socialLinkTextView.text = "@$socialNetworkLink"
                    binding.socialLinkTextView.visibility = View.VISIBLE
                }
            }
        }
        binding.apply {
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.notifications -> {
                        findNavController().navigate(R.id.action_myProfileFragment_to_notificationsFragment)
                        true
                    }

                    R.id.settings -> {
                        findNavController().navigate(R.id.action_myProfileFragment_to_settingsFragment)
                        true
                    }

                    else -> false
                }
            }
            editButton.setOnClickListener {
                findNavController().navigate(R.id.action_myProfileFragment_to_editProfileFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().apply {
                popBackStack(R.id.home_navigation, true)
                navigate(R.id.home_navigation)
            }
        }
    }

}