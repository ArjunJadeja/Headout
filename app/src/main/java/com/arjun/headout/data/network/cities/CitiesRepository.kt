package com.arjun.headout.data.network.cities

import com.arjun.headout.data.model.City

class CitiesRepository(private val dao: CitiesDao) {

    suspend fun getCitiesList(): List<City> {
        return dao.getCities()
    }

    suspend fun getCityDetails(cityId: String): City? {
        return dao.getCityDetails(cityId)
    }

}