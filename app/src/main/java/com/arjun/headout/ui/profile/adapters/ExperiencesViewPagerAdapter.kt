package com.arjun.headout.ui.profile.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arjun.headout.ui.profile.fragments.MyBookingsFragment
import com.arjun.headout.ui.profile.fragments.SavedExperiencesFragment

class ExperiencesViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyBookingsFragment()
            1 -> SavedExperiencesFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}