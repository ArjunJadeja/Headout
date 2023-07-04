package com.arjun.headout.data.network.profile

import android.net.Uri
import com.arjun.headout.data.model.HeadOuter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

class ProfileRepository(private val dao: ProfileDao) {

    val currentUser = FirebaseAuth.getInstance().currentUser

    suspend fun isUserRegistered(userUid: String): Boolean {
        return dao.isUserRegistered(userUid)
    }

    fun registerUser(
        userUid: String, displayName: String, email: String, profileImageUrl: String
    ) {
        dao.registerUser(userUid, displayName, email, profileImageUrl)
    }

    suspend fun profileSet() = dao.profileSet()

    suspend fun getMyProfile(): DocumentSnapshot? {
        return dao.getMyProfile()
    }

    fun uploadImage(imageUri: Uri) {
        dao.uploadImage(imageUri)
    }

    fun createProfile(
        name: String, cityId: String, cityName: String, languageCode: String, currencyCode: String
    ) = dao.createProfile(
        name = name,
        cityId = cityId,
        cityName = cityName,
        languageCode = languageCode,
        currencyCode = currencyCode
    )

    fun editProfile(
        name: String, socialNetwork: String, socialNetworkLink: String
    ) = dao.editProfile(
        name = name, socialNetwork = socialNetwork, socialNetworkLink = socialNetworkLink
    )

    fun updateCity(cityId: String, cityName: String) {
        dao.updateCity(cityId = cityId, cityName = cityName)
    }

    fun updateLanguage(languageCode: String) {
        dao.updateLanguage(languageCode = languageCode)
    }

    fun updateCurrency(currencyCode: String) {
        dao.updateCurrency(currencyCode = currencyCode)
    }

    fun saveExperience(expId: String) {
        dao.saveExperience(expId = expId)
    }

    fun removeExperience(expId: String) {
        dao.removeExperience(expId = expId)
    }

    suspend fun getSavedExperiencesIds(): MutableList<String> {
        return dao.getSavedExperiencesIds()
    }

    fun addBooking(bookingId: String) {
        dao.addBooking(bookingId = bookingId)
    }

    suspend fun getBookingsIds(): MutableList<String> {
        return dao.getBookingsIds()
    }

    fun savePostId(postId: String) {
        dao.savePostId(postId = postId)
    }

    fun removePostId(postId: String) {
        dao.removePostId(postId = postId)
    }

    suspend fun getMyPostsIds(): MutableList<String> {
        return dao.getMyPostsIds()
    }

    suspend fun getUserById(userId: String): HeadOuter {
        return dao.getUserById(userId = userId)
    }

}