package com.arjun.headout.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arjun.headout.data.local.AppDatabase
import com.arjun.headout.data.local.city.LocalCityRepository
import com.arjun.headout.data.local.experience.LocalExperienceRepository
import com.arjun.headout.data.local.preferences.PreferencesDataStore
import com.arjun.headout.data.model.Booking
import com.arjun.headout.data.model.City
import com.arjun.headout.data.model.Experience
import com.arjun.headout.data.model.ExperienceDetail
import com.arjun.headout.data.model.Subcategory
import com.arjun.headout.data.network.booking.BookingsDao
import com.arjun.headout.data.network.booking.BookingsRepository
import com.arjun.headout.data.network.cities.CitiesDao
import com.arjun.headout.data.network.cities.CitiesRepository
import com.arjun.headout.data.network.experience.ExperiencesDao
import com.arjun.headout.data.network.experience.ExperiencesRepository
import com.arjun.headout.util.cache.CityExpWorkerUtil
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    //    Network Status
    private val _isNetworkConnected = MutableLiveData<Boolean>()
    val isNetworkConnected: LiveData<Boolean> = _isNetworkConnected

    fun getNetwork(isNetworkConnected: Boolean) {
        _isNetworkConnected.value = isNetworkConnected
    }

    // Preference DataStore
    private val dataStore = PreferencesDataStore(application)

    fun saveData(key: String, value: String) {
        viewModelScope.launch {
            dataStore.saveData(key, value)
        }
    }

    suspend fun readData(key: String, defaultValue: String): String {
        return dataStore.readData(key, defaultValue)
    }

    fun clearPreferences() {
        viewModelScope.launch {
            dataStore.clearAllData()
        }
    }

    // Local Data
    private val localCityRepository: LocalCityRepository
    private val localExperienceRepository: LocalExperienceRepository

    // Remote Data
    private val citiesRepository: CitiesRepository
    private val experiencesRepository: ExperiencesRepository
    private val bookingsRepository: BookingsRepository

    // Initialize local repositories
    init {
        val appDatabase = AppDatabase.getDatabase(application)
        val localCityDao = appDatabase.localCityDao()
        val localExperienceDao = appDatabase.localExperienceDao()
        localCityRepository = LocalCityRepository(localCityDao)
        localExperienceRepository = LocalExperienceRepository(localExperienceDao)
    }

    // Initialize remote repositories
    init {
        val citiesDao = CitiesDao()
        val experiencesDao = ExperiencesDao()
        val bookingsDao = BookingsDao()
        citiesRepository = CitiesRepository(citiesDao)
        experiencesRepository = ExperiencesRepository(experiencesDao)
        bookingsRepository = BookingsRepository(bookingsDao)
    }

    // LocalCityRepository functions

    suspend fun saveCityLocally(city: City) {
        localCityRepository.insertCity(city)
    }

    suspend fun deleteCitiesLocally() {
        localCityRepository.deleteAllCities()
    }

    suspend fun getCityLocally(cityId: String): City? {
        return localCityRepository.getCity(cityId)
    }

    // LocalExperienceRepository functions

    suspend fun saveExperiencesLocally(experiences: List<Experience>) {
        localExperienceRepository.insertExperiences(experiences)
    }

    suspend fun deleteExperiencesLocally() {
        localExperienceRepository.deleteAllExperiences()
    }

    suspend fun getExperiencesByIdsLocally(experienceIds: List<String>) =
        localExperienceRepository.getExperiencesByIds(experienceIds)

    suspend fun getExperiencesLocally(): List<Experience>? {
        return localExperienceRepository.getAllExperiences()
    }

    // Remote data handling

    suspend fun getCitiesListRemote(): List<City> {
        return citiesRepository.getCitiesList()
    }

    suspend fun getCityRemote(cityId: String): City? {
        return citiesRepository.getCityDetails(cityId)
    }

    suspend fun getExperiencesByIdsRemote(experienceIds: List<String>): List<Experience> {
        return experiencesRepository.getExperiencesByIds(experienceIds)
    }

    suspend fun getExperienceDetails(): ExperienceDetail? {
        return experiencesRepository.getExperienceDetails()
    }

    fun fetchAndSaveDataFromRemote(context: Context, cityId: String) {
        CityExpWorkerUtil.enqueueCityExpWorker(context = context, cityId = cityId)
    }

    // Bookings
    fun bookExperience(booking: Booking) {
        bookingsRepository.bookExperience(booking)
    }

    fun updateBookingPostStatus(selectedBooking: Booking, hasPosted: Boolean) {
        bookingsRepository.updateBookingPostStatus(
            selectedBooking = selectedBooking, hasPosted = hasPosted
        )
    }

    suspend fun getBookingsByIds(bookingsIds: List<String>): List<Booking> {
        return bookingsRepository.getBookingsByIds(bookingsIds = bookingsIds)
    }

    // Selected Items
    val selectedSubcategory = MutableLiveData<Subcategory>()

    val selectedExperience = MutableLiveData<Experience>()

    val selectedBooking = MutableLiveData<Booking>()

}