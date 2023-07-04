package com.arjun.headout.ui.experience.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arjun.headout.R
import com.arjun.headout.databinding.FragmentExperienceImagesBinding
import com.arjun.headout.util.ShimmerUtil

class ExperienceImagesFragment : Fragment(R.layout.fragment_experience_images) {

    private lateinit var binding: FragmentExperienceImagesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExperienceImagesBinding.bind(view)

        val imageUrl = arguments?.getString("imageUrl")

        ShimmerUtil.loadImageWithShimmer(
            imageView = binding.imageView,
            imageUrl = imageUrl
        )

    }

    companion object {
        fun newInstance(imageUrl: String): ExperienceImagesFragment {
            val fragment = ExperienceImagesFragment()
            val args = Bundle()
            args.putString("imageUrl", imageUrl)
            fragment.arguments = args
            return fragment
        }
    }
}