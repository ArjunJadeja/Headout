package com.arjun.headout.data.local.city

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalCityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: LocalCity)

    @Query("SELECT * FROM local_city WHERE id = :cityId")
    suspend fun getCity(cityId: String): LocalCity?

    @Query("DELETE FROM local_city")
    suspend fun deleteAllCities()
}