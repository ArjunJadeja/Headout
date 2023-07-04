package com.arjun.headout.ui.profile.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.DEFAULT_PREFERENCE
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.Booking
import com.arjun.headout.databinding.FragmentMyBookingsBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.ui.profile.adapters.AddPostInterface
import com.arjun.headout.ui.profile.adapters.MyBookingsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyBookingsFragment : Fragment(R.layout.fragment_my_bookings), AddPostInterface {

    private lateinit var binding: FragmentMyBookingsBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var languageCode = DEFAULT_PREFERENCE
    private lateinit var adapter: MyBookingsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyBookingsBinding.bind(view)

        lifecycleScope.launch {
            languageCode =
                mainViewModel.readData(PreferencesHelper.SELECTED_LANGUAGE_CODE, DEFAULT_PREFERENCE)

            val bookingsIds = profileViewModel.getBookingsIds()
            val myBookings = bookingsIds.let {
                mainViewModel.getBookingsByIds(it)
            }

            withContext(Dispatchers.Main) {
                if (myBookings.isNotEmpty()) {
                    setAdapter(myBookings.reversed())
                    binding.myBookingsRecyclerView.visibility = View.VISIBLE
                    binding.emptyListLayout.visibility = View.GONE
                } else {
                    binding.myBookingsRecyclerView.visibility = View.GONE
                    binding.emptyListLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setAdapter(myBookings: List<Booking>) {
        adapter = MyBookingsAdapter(myBookings, languageCode, this@MyBookingsFragment)
        binding.myBookingsRecyclerView.adapter = adapter
        binding.myBookingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun shareExperienceLicked(booking: Booking) {
        mainViewModel.selectedBooking.value = booking
        findNavController().navigate(R.id.action_myProfileFragment_to_addPostFragment)
    }

}