package com.arjun.headout.ui.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arjun.headout.R
import com.arjun.headout.data.model.Booking
import com.arjun.headout.databinding.MyBookingsItemBinding
import com.arjun.headout.util.ShimmerUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyBookingsAdapter(
    private val itemList: List<Booking>,
    private val languageCode: String,
    private val listener: AddPostInterface
) : RecyclerView.Adapter<MyBookingsAdapter.MyBookingsViewHolder>() {

    class MyBookingsViewHolder(var binding: MyBookingsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBookingsViewHolder {
        return MyBookingsViewHolder(
            MyBookingsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyBookingsViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.apply {

            item.experience?.mediaUrls?.images?.getOrNull(0)?.let { imageUrl ->
                ShimmerUtil.loadImageWithShimmer(
                    imageView = holder.binding.experienceImageView,
                    imageUrl = imageUrl
                )
            }

            val bookingDate = Date(item.bookingDetails.experienceDate)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(bookingDate)
            val status = getStatus(context = holder.itemView.context, selectedDate = bookingDate)

            if (!item.hasPosted && status == holder.itemView.context.getString(R.string.completed)) {
                shareExperienceButton.visibility = View.VISIBLE
            }

            experienceTextView.text = item.experience?.title?.get(languageCode) ?: ""
            dateTextView.text =
                "${holder.itemView.context.getString(R.string.booking_date)} : $formattedDate"
            totalTicketsTextView.text =
                "${holder.itemView.context.getString(R.string.number_of_tickets)} : ${item.bookingDetails.totalTicketsNumber}"
            statusTextView.text = "${holder.itemView.context.getString(R.string.status)} : $status"
            contactNameTextView.text = "${holder.itemView.context.getString(R.string.contact_name)} : ${item.bookingDetails.contactName}"
            contactEmailTextView.text = "${holder.itemView.context.getString(R.string.contact_email)} : ${item.bookingDetails.contactEmail}"
            contactPhoneTextView.text = "${holder.itemView.context.getString(R.string.contact_number)} : ${item.bookingDetails.contactPhone}"

            shareExperienceButton.setOnClickListener {
                listener.shareExperienceLicked(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private fun getStatus(context: Context, selectedDate: Date): String {
        val calendar = Calendar.getInstance()

        calendar.time = selectedDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val selectedDateAtMidnight = calendar.time

        calendar.time = Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val currentDateAtMidnight = calendar.time

        return when {
            selectedDateAtMidnight.after(currentDateAtMidnight) -> context.getString(R.string.upcoming)
            selectedDateAtMidnight.before(currentDateAtMidnight) -> context.getString(R.string.completed)
            else -> context.getString(R.string.happening_today)
        }
    }

}

interface AddPostInterface {
    fun shareExperienceLicked(booking: Booking)
}