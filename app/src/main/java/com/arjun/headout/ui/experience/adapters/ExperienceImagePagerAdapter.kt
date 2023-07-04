package com.arjun.headout.ui.experience.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arjun.headout.ui.experience.fragments.ExperienceImagesFragment

class ExperienceImagePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val imageUrls: List<String>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    override fun createFragment(position: Int): Fragment {
        return ExperienceImagesFragment.newInstance(imageUrls[position])
    }
}