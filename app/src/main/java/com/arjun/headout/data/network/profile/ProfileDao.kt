package com.arjun.headout.data.network.profile

import android.net.Uri
import com.arjun.headout.data.model.HeadOuter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileDao {

    private val usersCollection = FirebaseFirestore.getInstance().collection("users")

    suspend fun isUserRegistered(userUid: String): Boolean {
        val documentSnapshot = usersCollection.document(userUid).get().await()
        return documentSnapshot.exists()
    }

    fun registerUser(userUid: String, displayName: String, email: String, profileImageUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = HeadOuter(
                uid = userUid,
                name = displayName,
                email = email,
                username = email,
                profileImageUrl = profileImageUrl,
                credits = 50
            )
            usersCollection.document(userUid).set(user).await()
        }
    }

    suspend fun profileSet(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val documentSnapshot = usersCollection.document(currentUser!!.uid).get().await()
            .toObject(HeadOuter::class.java)
        return documentSnapshot!!.cityId?.isNotEmpty() ?: false
    }

    suspend fun getMyProfile(): DocumentSnapshot? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return usersCollection.document(currentUser!!.uid).get().await()
    }

    fun uploadImage(imageUri: Uri) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val storageRef = FirebaseStorage.getInstance().getReference("profile_images")
        CoroutineScope(Dispatchers.IO).launch {
            storageRef.child(currentUser!!.uid).putFile(imageUri).addOnSuccessListener {
                    storageRef.child(currentUser.uid).downloadUrl.addOnSuccessListener { cloudStorageUri ->
                        updateImageUrl(cloudStorageUri.toString())
                    }
                }.await()
        }
    }

    private fun updateImageUrl(imageUrl: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            usersCollection.document(currentUser!!.uid).update(mapOf("profileImageUrl" to imageUrl))
                .await()
        }
    }

    fun createProfile(
        name: String, cityId: String, cityName: String, languageCode: String, currencyCode: String
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            usersCollection.document(currentUser!!.uid).update(
                mapOf(
                    "name" to name,
                    "cityId" to cityId,
                    "cityName" to cityName,
                    "languageCode" to languageCode,
                    "currencyCode" to currencyCode
                )
            ).await()
        }
    }

    fun editProfile(
        name: String, socialNetwork: String, socialNetworkLink: String
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            usersCollection.document(currentUser!!.uid).update(
                mapOf(
                    "name" to name,
                    "socialNetwork" to socialNetwork,
                    "socialNetworkLink" to socialNetworkLink
                )
            ).await()
        }
    }

    fun updateCity(cityId: String, cityName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            usersCollection.document(currentUser!!.uid).update(
                mapOf(
                    "cityId" to cityId, "cityName" to cityName
                )
            ).await()
        }
    }

    fun updateLanguage(languageCode: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            usersCollection.document(currentUser!!.uid).update(
                mapOf(
                    "languageCode" to languageCode
                )
            ).await()
        }
    }

    fun updateCurrency(currencyCode: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            usersCollection.document(currentUser!!.uid).update(
                mapOf(
                    "currencyCode" to currencyCode
                )
            ).await()
        }
    }

    // Saved Experiences

    fun saveExperience(expId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            val user = usersCollection.document(currentUser!!.uid).get().await()
                .toObject(HeadOuter::class.java)!!
            val expAdded = user.likedExperiences.contains(expId)
            if (!expAdded) {
                user.likedExperiences.add(expId)
            }
            usersCollection.document(currentUser.uid).set(user).await()
        }
    }

    fun removeExperience(expId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            val user = usersCollection.document(currentUser!!.uid).get().await()
                .toObject(HeadOuter::class.java)!!
            val expAdded = user.likedExperiences.contains(expId)
            if (expAdded) {
                user.likedExperiences.remove(expId)
            }
            usersCollection.document(currentUser.uid).set(user).await()
        }
    }

    suspend fun getSavedExperiencesIds(): MutableList<String> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val documentSnapshot = usersCollection.document(currentUser!!.uid).get().await()
            .toObject(HeadOuter::class.java)
        return documentSnapshot?.likedExperiences ?: mutableListOf(currentUser.uid)
    }

    // Bookings

    fun addBooking(bookingId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            val user = usersCollection.document(currentUser!!.uid).get().await()
                .toObject(HeadOuter::class.java)!!
            val expAdded = user.bookings.contains(bookingId)
            if (!expAdded) {
                user.bookings.add(bookingId)
            }
            usersCollection.document(currentUser.uid).set(user).await()
        }
    }

    suspend fun getBookingsIds(): MutableList<String> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val documentSnapshot = usersCollection.document(currentUser!!.uid).get().await()
            .toObject(HeadOuter::class.java)
        return documentSnapshot?.bookings ?: mutableListOf(currentUser.uid)
    }

    // Posts

    fun savePostId(postId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            val user = usersCollection.document(currentUser!!.uid).get().await()
                .toObject(HeadOuter::class.java)!!
            val postAdded = user.posts.contains(postId)
            if (!postAdded) {
                user.posts.add(postId)
            }
            usersCollection.document(currentUser.uid).set(user).await()
        }
    }

    fun removePostId(postId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        CoroutineScope(Dispatchers.IO).launch {
            val user = usersCollection.document(currentUser!!.uid).get().await()
                .toObject(HeadOuter::class.java)!!
            val postAdded = user.posts.contains(postId)
            if (postAdded) {
                user.posts.remove(postId)
            }
            usersCollection.document(currentUser.uid).set(user).await()
        }
    }

    suspend fun getMyPostsIds(): MutableList<String> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val documentSnapshot = usersCollection.document(currentUser!!.uid).get().await()
            .toObject(HeadOuter::class.java)
        return documentSnapshot?.posts ?: mutableListOf(currentUser.uid)
    }

    // get user
    suspend fun getUserById(userId: String): HeadOuter {
        val documentSnapshot = usersCollection.document(userId).get().await()
        return documentSnapshot.toObject(HeadOuter::class.java) ?: HeadOuter()
    }

}