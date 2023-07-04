package com.arjun.headout.data.local.city

import com.arjun.headout.data.model.City

class LocalCityRepository(private val localCityDao: LocalCityDao) {

    suspend fun insertCity(city: City) {
        val localCity = LocalCity(
            id = city.id,
            name = city.name,
            country = city.country,
            imageUrl = city.imageUrl,
            bannerUrl = city.bannerUrl,
            latitude = city.latitude,
            longitude = city.longitude,
            discoverable = city.discoverable,
            categories = city.categories
        )
        localCityDao.insertCity(localCity)
    }

    suspend fun getCity(cityId: String): City? {
        val localCity = localCityDao.getCity(cityId) ?: return null
        return City(
            id = localCity.id,
            name = localCity.name,
            country = localCity.country,
            imageUrl = localCity.imageUrl,
            bannerUrl = localCity.bannerUrl,
            latitude = localCity.latitude,
            longitude = localCity.longitude,
            discoverable = localCity.discoverable,
            categories = localCity.categories
        )
    }

    suspend fun deleteAllCities() {
        localCityDao.deleteAllCities()
    }
}