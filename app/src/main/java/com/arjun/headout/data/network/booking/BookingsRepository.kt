package com.arjun.headout.data.network.booking

import com.arjun.headout.data.model.Booking

class BookingsRepository(private val dao: BookingsDao) {

    fun bookExperience(booking: Booking) {
        dao.bookExperience(booking)
    }

    fun updateBookingPostStatus(selectedBooking: Booking, hasPosted: Boolean) {
        dao.updateBookingPostStatus(selectedBooking = selectedBooking, hasPosted = hasPosted)
    }

    suspend fun getBookingsByIds(bookingsIds: List<String>): List<Booking> {
        return dao.getBookingsByIds(bookingsIds = bookingsIds)
    }

}