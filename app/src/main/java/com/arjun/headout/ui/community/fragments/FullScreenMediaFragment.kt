package com.arjun.headout.ui.community.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.arjun.headout.R
import com.arjun.headout.databinding.FragmentFullScreenMediaBinding
import com.arjun.headout.ui.community.CommunityViewModel
import com.arjun.headout.ui.community.adapters.PostImagePagerAdapter

class FullScreenMediaFragment : Fragment(R.layout.fragment_full_screen_media) {

    private lateinit var binding: FragmentFullScreenMediaBinding

    private val communityViewModel: CommunityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFullScreenMediaBinding.bind(view)

        communityViewModel.selectedPostsImages.observe(viewLifecycleOwner) { imageUrls ->
            communityViewModel.selectedImagePosition.observe(viewLifecycleOwner) { position ->
                binding.apply {
                    viewPager.adapter =
                        PostImagePagerAdapter(childFragmentManager, lifecycle, imageUrls)
                    viewPager.setCurrentItem(position, false)
                }
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

    }

}