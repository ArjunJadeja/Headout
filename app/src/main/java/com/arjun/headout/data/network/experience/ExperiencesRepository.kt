package com.arjun.headout.data.network.experience

import com.arjun.headout.data.model.Experience
import com.arjun.headout.data.model.ExperienceDetail

class ExperiencesRepository(private val dao: ExperiencesDao) {

    suspend fun getExperienceDetails(): ExperienceDetail? {
        return dao.getExperienceDetails()
    }

    suspend fun getExperiencesByIds(experienceIds: List<String>): List<Experience> {
        return dao.getExperiencesByIds(experienceIds)
    }

}