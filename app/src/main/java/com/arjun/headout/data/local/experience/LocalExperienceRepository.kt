package com.arjun.headout.data.local.experience

import com.arjun.headout.data.model.Experience

class LocalExperienceRepository(private val localExperienceDao: LocalExperienceDao) {

    suspend fun insertExperiences(experiences: List<Experience>) {
        val localExperiences = experiences.map { experience ->
            LocalExperience(id = experience.id ?: "",
                title = experience.title?.mapValues { entry -> entry.value.toString() },
                description = experience.description?.mapValues { entry -> entry.value.toString() },
                mediaUrls = experience.mediaUrls,
                highlights = experience.highlights?.mapValues { entry -> entry.value.toString() })
        }
        localExperienceDao.insertExperiences(localExperiences)
    }

    suspend fun getExperiencesByIds(experienceIds: List<String>): List<Experience> {
        val localExperiences = localExperienceDao.getExperiencesByIds(experienceIds)
        return localExperiences.map { localExperience ->
            Experience(
                id = localExperience.id,
                title = localExperience.title,
                description = localExperience.description,
                mediaUrls = localExperience.mediaUrls,
                highlights = localExperience.highlights
            )
        }
    }

    suspend fun getAllExperiences(): List<Experience>? {
        val localExperiences = localExperienceDao.getAllExperiences()
        return localExperiences?.map { localExperience ->
            Experience(
                id = localExperience.id,
                title = localExperience.title,
                description = localExperience.description,
                mediaUrls = localExperience.mediaUrls,
                highlights = localExperience.highlights
            )
        }
    }

    suspend fun deleteAllExperiences() {
        localExperienceDao.deleteAllExperiences()
    }
}