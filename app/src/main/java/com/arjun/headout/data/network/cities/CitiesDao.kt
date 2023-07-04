package com.arjun.headout.data.network.cities

import com.arjun.headout.data.model.City
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CitiesDao {

    private val citiesCollection = FirebaseFirestore.getInstance().collection("cities")

    suspend fun getCities(): List<City> {
        val querySnapshot = citiesCollection.get().await()
        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(City::class.java)
        }
    }

    suspend fun getCityDetails(cityId: String): City? {
        val documentSnapshot = citiesCollection.document(cityId).get().await()
        return documentSnapshot.toObject(City::class.java)
    }

}