package com.arjun.headout.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arjun.headout.data.local.city.LocalCity
import com.arjun.headout.data.local.city.LocalCityDao
import com.arjun.headout.data.local.experience.LocalExperience
import com.arjun.headout.data.local.experience.LocalExperienceDao

@Database(entities = [LocalCity::class, LocalExperience::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun localCityDao(): LocalCityDao
    abstract fun localExperienceDao(): LocalExperienceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}