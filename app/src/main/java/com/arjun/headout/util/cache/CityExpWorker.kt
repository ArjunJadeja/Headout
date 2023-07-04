package com.arjun.headout.util.cache

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.util.ProgressLoadingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityExpWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val mainViewModel = MainViewModel(applicationContext as Application)

    override suspend fun doWork(): Result {
        // 1. Get the cityId from inputData
        val cityId = inputData.getString("cityId") ?: return Result.failure()

        // 2. Fetch city details from the network
        val city = mainViewModel.getCityRemote(cityId) ?: return Result.failure()

        // 3. Delete old city, experiences and video from the local database
        mainViewModel.deleteCitiesLocally()
        mainViewModel.deleteExperiencesLocally()
        VideoStorageUtil.deleteCityVideoUri(applicationContext)

        mainViewModel.saveData(PreferencesHelper.LOCAL_DATA_SAVED, "false")
        mainViewModel.saveData(PreferencesHelper.LOCAL_VIDEO_SAVED, "false")

        // 4. Insert the fetched city into the local database
        mainViewModel.saveCityLocally(city)

        // 5. Fetch experiences from the network
        val subcategoryList = city.categories?.flatMap { category ->
            category?.subcategories.orEmpty()
        }!!.mapNotNull { it }

        val experienceIds = subcategoryList.flatMap { it.experiences.orEmpty() }.mapNotNull { it }
        val experiences = mainViewModel.getExperiencesByIdsRemote(experienceIds)

        // 6. Insert fetched experiences into the local database
        mainViewModel.saveExperiencesLocally(experiences)

        mainViewModel.saveData(PreferencesHelper.LOCAL_DATA_SAVED, "true")

        withContext(Dispatchers.Main) {
            ProgressLoadingHelper.dismissProgressBar()
        }

        // 7. Return success
        return Result.success()
    }
}