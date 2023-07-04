package com.arjun.headout.util.cache

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

object CityExpWorkerUtil {

    private const val UNIQUE_WORK_NAME = "cacheCityAndExperiences"

    fun enqueueCityExpWorker(context: Context, cityId: String) {
        val workManager = WorkManager.getInstance(context)

        // Cancel previous work requests with the same unique name
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)

        // Create inputData for the worker
        val inputData = workDataOf("cityId" to cityId)

        // Create a new work request
        val workRequest = OneTimeWorkRequestBuilder<CityExpWorker>().setInputData(inputData).build()

        // Enqueue the new work request with the same unique name
        workManager.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
    }
}