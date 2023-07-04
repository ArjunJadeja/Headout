package com.arjun.headout.ui.community.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arjun.headout.R
import com.arjun.headout.databinding.FragmentPostImagesBinding
import com.arjun.headout.ui.experience.fragments.ExperienceImagesFragment
import com.arjun.headout.util.ShimmerUtil


class PostImagesFragment : Fragment(R.layout.fragment_post_images) {

    private lateinit var binding: FragmentPostImagesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPostImagesBinding.bind(view)

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