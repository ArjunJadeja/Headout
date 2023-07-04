package com.arjun.headout.data.network.experience

import com.arjun.headout.data.model.Experience
import com.arjun.headout.data.model.ExperienceDetail
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExperiencesDao {

    private val experiencesCollection = FirebaseFirestore.getInstance().collection("experiences")
    private val expDetailsCollection =
        FirebaseFirestore.getInstance().collection("experience_details")

    suspend fun getExperienceDetails(): ExperienceDetail? {
        val documentSnapshot = expDetailsCollection.document("dummy").get().await()
        return documentSnapshot.toObject(ExperienceDetail::class.java)
    }

    suspend fun getExperiencesByIds(experienceIds: List<String>): List<Experience> {
        val experiences = mutableListOf<Experience>()
        for (id in experienceIds) {
            val documentSnapshot = experiencesCollection.document(id).get().await()
            documentSnapshot.toObject(Experience::class.java)?.let { experience ->
                experiences.add(experience)
            }
        }
        return experiences
    }

}