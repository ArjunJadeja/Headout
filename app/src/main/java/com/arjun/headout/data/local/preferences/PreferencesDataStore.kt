package com.arjun.headout.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class PreferencesDataStore(context: Context) {

    private val appContext = context.applicationContext

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")
    }

    suspend fun saveData(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        appContext.dataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    suspend fun readData(key: String, defaultValue: String): String {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = appContext.dataStore.data.first()
        return preferences[dataStoreKey] ?: defaultValue
    }

    suspend fun clearAllData() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}