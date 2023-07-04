package com.arjun.headout.data.local.experience

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalExperienceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiences(experiences: List<LocalExperience>)

    @Query("SELECT * FROM local_experience")
    suspend fun getAllExperiences(): List<LocalExperience>?

    @Query("SELECT * FROM local_experience WHERE id IN (:experienceIds)")
    suspend fun getExperiencesByIds(experienceIds: List<String>): List<LocalExperience>

    @Query("DELETE FROM local_experience")
    suspend fun deleteAllExperiences()
}