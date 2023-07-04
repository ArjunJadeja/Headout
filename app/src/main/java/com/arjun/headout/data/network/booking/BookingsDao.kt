package com.arjun.headout.data.network.booking

import com.arjun.headout.data.model.Booking
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BookingsDao {

    private val bookingsCollection = FirebaseFirestore.getInstance().collection("bookings")

    fun bookExperience(booking: Booking) {
        CoroutineScope(Dispatchers.IO).launch {
            bookingsCollection.document(booking.id).set(booking).await()
        }
    }

    fun updateBookingPostStatus(selectedBooking: Booking, hasPosted: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val booking = bookingsCollection.document(selectedBooking.id).get().await()
                .toObject(Booking::class.java)!!
            booking.hasPosted = hasPosted
            bookingsCollection.document(selectedBooking.id).set(booking).await()
        }
    }

    suspend fun getBookingsByIds(bookingsIds: List<String>): List<Booking> {
        val bookings = mutableListOf<Booking>()
        for (id in bookingsIds) {
            val documentSnapshot = bookingsCollection.document(id).get().await()
            documentSnapshot.toObject(Booking::class.java)?.let { booking ->
                bookings.add(booking)
            }
        }
        return bookings
    }
}