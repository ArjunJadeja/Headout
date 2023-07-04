package com.arjun.headout.ui.experience.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.text.Editable
import android.text.TextWatcher
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
import com.arjun.headout.data.model.Booking
import com.arjun.headout.data.model.BookingDetails
import com.arjun.headout.data.model.Experience
import com.arjun.headout.data.model.ExperienceDetail
import com.arjun.headout.databinding.FragmentExperienceBookingBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.util.ProgressLoadingHelper
import com.arjun.headout.util.ShimmerUtil
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ExperienceBookingFragment : Fragment(R.layout.fragment_experience_booking) {

    private lateinit var binding: FragmentExperienceBookingBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var experience: Experience? = null
    private var experienceDetail: ExperienceDetail? = null

    private var userId = DEFAULT_PREFERENCE
    private var languageCode = DEFAULT_PREFERENCE
    private var currencyCode = DEFAULT_PREFERENCE
    private var currencySymbol = DEFAULT_PREFERENCE

    private var experienceDate: Long = 0
    private var ticketPrice = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExperienceBookingBinding.bind(view)

        mainViewModel.selectedExperience.observe(viewLifecycleOwner) {
            it?.let { experience ->
                this.experience = experience
                lifecycleScope.launch {
                    userId = mainViewModel.readData(PreferencesHelper.USER_ID, DEFAULT_PREFERENCE)
                    languageCode = mainViewModel.readData(
                        PreferencesHelper.SELECTED_LANGUAGE_CODE, DEFAULT_PREFERENCE
                    )
                    currencyCode = mainViewModel.readData(
                        PreferencesHelper.SELECTED_CURRENCY_CODE, DEFAULT_PREFERENCE
                    )
                    ShimmerUtil.loadImageWithShimmer(
                        imageView = binding.experienceImageView,
                        imageUrl = experience.mediaUrls?.images?.first()
                    )
                    binding.experienceTextView.text = experience.title?.get(languageCode)

                    currencySymbol =
                        CurrencyHelper.getCurrencyByCode(currencyCode)?.symbol.toString()

                    binding.totalPriceTextView.text =
                        "${getString(R.string.total_price)} : $currencySymbol $ticketPrice"
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
            }
        }

        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            selectDateTextView.setOnClickListener {
                setupDatePicker()
            }
            binding.numTicketsEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // This method is called before the text is changed.
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // This method is called when the text is being changed.
                }

                override fun afterTextChanged(s: Editable?) {
                    val ticketInput = s.toString().trim()
                    val noOfTickets = if (ticketInput == "" || ticketInput.isBlank()) {
                        0
                    } else {
                        ticketInput.toInt()
                    }
                    binding.totalPriceTextView.text =
                        "${getString(R.string.total_price)} : $currencySymbol ${ticketPrice * noOfTickets}"
                }
            })
            bookNowButton.setOnClickListener {

                val totalTicketsNumber = binding.numTicketsEditText.text.toString().trim()
                val contactName = binding.contactNameEditText.text.toString().trim()
                val contactEmail = binding.contactEmailEditText.text.toString().trim()
                val contactPhoneEditText = binding.contactPhoneEditText.text.toString().trim()
                val additionalRequests = binding.additionalRequestsEditText.text.toString().trim()

                val bookingId = userId + experience?.id.toString() + System.currentTimeMillis()

                if (binding.numTicketsEditText.text.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.add_valid_ticket_number),
                        Toast.LENGTH_LONG
                    ).show()
                } else if (inputValid(
                        totalTicketsNumber,
                        experienceDate,
                        contactName,
                        contactEmail,
                        contactPhoneEditText
                    )
                ) {
                    ProgressLoadingHelper.showProgressBar(
                        requireContext(),
                        getString(R.string.booking_progress)
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        val newBooking = Booking(
                            id = bookingId,
                            userId = userId,
                            bookingTime = System.currentTimeMillis(),
                            totalPrice = ticketPrice * totalTicketsNumber.toInt(),
                            currency = currencyCode,
                            hasPosted = false,
                            experience = experience,
                            bookingDetails = BookingDetails(
                                experienceDate = experienceDate,
                                totalTicketsNumber = totalTicketsNumber.toInt(),
                                contactName = contactName,
                                contactEmail = contactEmail,
                                contactPhone = contactPhoneEditText,
                                additionalRequests = additionalRequests
                            )
                        )
                        mainViewModel.bookExperience(newBooking)
                        profileViewModel.addBooking(bookingId)
                        ProgressLoadingHelper.dismissProgressBar()
                        showConfirmationDialog()
                    }, 1000)
                }
            }
        }
    }

    private fun showConfirmationDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.booking_confirmed))
            setMessage(getString(R.string.booking_message))
            setCancelable(false)
            setPositiveButton(getString(R.string.okay)) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigate(R.id.action_experienceBookingFragment3_to_experienceDetailsFragment3)
            }
        }.create()

        dialog.show()
    }

    private fun inputValid(
        totalTicketsNumber: String?,
        experienceDate: Long?,
        contactName: String?,
        contactEmail: String?,
        contactPhoneEditText: String?,
    ): Boolean {

        if (totalTicketsNumber != null) {
            if (totalTicketsNumber.toInt() > 99 || totalTicketsNumber.toInt() <= 0) {
                Toast.makeText(
                    requireContext(), getString(R.string.add_valid_ticket_number), Toast.LENGTH_LONG
                ).show()
                return false
            }
        }
        if (experienceDate == 0L || experienceDate == null) {
            Toast.makeText(
                requireContext(), getString(R.string.select_valid_date), Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (contactName.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(), getString(R.string.enter_contact_name), Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (contactEmail.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(), getString(R.string.enter_contact_email), Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (contactPhoneEditText.isNullOrEmpty() || contactPhoneEditText.length < 10) {
            Toast.makeText(
                requireContext(), getString(R.string.enter_valid_phone_number), Toast.LENGTH_LONG
            ).show()
            return false
        }

        return true
    }

    private fun setupDatePicker() {
        val now = LocalDate.now()
        val startOfMonth = now.withDayOfMonth(1)
        val endOfMonthPlusTwoMonths =
            now.plusMonths(2).withDayOfMonth(now.plusMonths(2).lengthOfMonth())

        val calendarConstraints = CalendarConstraints.Builder()
            .setStart(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .setEnd(
                endOfMonthPlusTwoMonths.atStartOfDay(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli()
            ).setValidator(object : CalendarConstraints.DateValidator {
                override fun describeContents(): Int {
                    return 0
                }

                override fun writeToParcel(p0: Parcel, p1: Int) {}

                override fun isValid(date: Long): Boolean {
                    val selectedDate =
                        Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
                    return !selectedDate.isBefore(now)
                }
            }).build()

        val datePickerBuilder =
            MaterialDatePicker.Builder.datePicker().setTitleText(getString(R.string.select_a_date))
                .setCalendarConstraints(calendarConstraints)

        // Create a Calendar instance and set it to today's date
        val calendar = Calendar.getInstance()
        datePickerBuilder.setSelection(calendar.timeInMillis)

        val datePicker = datePickerBuilder.build()

        // Add a positive button listener to handle the selected date
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate)
            experienceDate = selection
            binding.selectDateTextView.setText(formattedDate)
        }

        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

}