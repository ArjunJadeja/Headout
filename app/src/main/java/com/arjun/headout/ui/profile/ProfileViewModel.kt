package com.arjun.headout.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.arjun.headout.data.model.HeadOuter
import com.arjun.headout.data.network.profile.ProfileDao
import com.arjun.headout.data.network.profile.ProfileRepository
import com.google.firebase.firestore.DocumentSnapshot

class ProfileViewModel : ViewModel() {

    private val repository: ProfileRepository

    init {
        val dao = ProfileDao()
        repository = ProfileRepository(dao)
    }

    val currentUser = repository.currentUser

    suspend fun isUserRegistered(userUid: String): Boolean {
        return repository.isUserRegistered(userUid)
    }

    fun registerUser(
        userUid: String,
        displayName: String,
        email: String,
        profileImageUrl: String
    ) {
        repository.registerUser(userUid, displayName, email, profileImageUrl)
    }

    suspend fun profileSet(): Boolean {
        return repository.profileSet()
    }

    suspend fun getMyProfile(): DocumentSnapshot? {
        return repository.getMyProfile()
    }

    fun uploadImage(imageUri: Uri) {
        repository.uploadImage(imageUri)
    }

    fun createProfile(
        name: String,
        cityId: String,
        cityName: String,
        languageCode: String,
        currencyCode: String
    ) = repository.createProfile(
        name = name,
        cityId = cityId,
        cityName = cityName,
        languageCode = languageCode,
        currencyCode = currencyCode
    )

    fun editProfile(
        name: String,
        socialNetwork: String,
        socialNetworkLink: String
    ) {
        repository.editProfile(
            name = name,
            socialNetwork = socialNetwork,
            socialNetworkLink = socialNetworkLink
        )
    }

    fun updateCity(cityId: String, cityName: String) {
        repository.updateCity(cityId = cityId, cityName = cityName)
    }

    fun updateLanguage(languageCode: String) {
        repository.updateLanguage(languageCode = languageCode)
    }

    fun updateCurrency(currencyCode: String) {
        repository.updateCurrency(currencyCode = currencyCode)
    }

    fun saveExperience(expId: String) {
        repository.saveExperience(expId = expId)
    }

    fun removeExperience(expId: String) {
        repository.removeExperience(expId = expId)
    }

    suspend fun getSavedExperiencesIds(): MutableList<String> {
        return repository.getSavedExperiencesIds()
    }

    fun addBooking(bookingId: String) {
        repository.addBooking(bookingId = bookingId)
    }

    suspend fun getBookingsIds(): MutableList<String> {
        return repository.getBookingsIds()
    }

    // Posts

    fun savePostId(postId: String) {
        repository.savePostId(postId = postId)
    }

    fun removePostId(postId: String) {
        repository.removePostId(postId = postId)
    }

    suspend fun getMyPostsIds(): MutableList<String> {
        return repository.getMyPostsIds()
    }

    suspend fun getUserById(userId: String): HeadOuter {
        return repository.getUserById(userId = userId)
    }

}