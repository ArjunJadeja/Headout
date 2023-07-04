package com.arjun.headout.util.cache

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.ui.MainViewModel

class SaveVideoWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val mainViewModel = MainViewModel(applicationContext as Application)

    override suspend fun doWork(): Result {
        val videoUrl = inputData.getString("videoUrl") ?: return Result.failure()

        return try {
            VideoStorageUtil.downloadAndSaveCityVideo(applicationContext, videoUrl)
            Log.e("SaveVideoWorker", "Success downloading video")
            mainViewModel.saveData(PreferencesHelper.LOCAL_VIDEO_SAVED, "true")
            Result.success()
        } catch (e: Exception) {
            Log.e("SaveVideoWorker", "Error downloading video", e)
            Result.failure()
        }
    }
}