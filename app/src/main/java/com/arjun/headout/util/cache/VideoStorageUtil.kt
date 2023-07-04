package com.arjun.headout.util.cache

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileOutputStream
import java.io.IOException

object VideoStorageUtil {

    private const val CITY_VIDEO_FILENAME = "city_video.mp4"

    suspend fun downloadAndSaveCityVideo(context: Context, videoUrl: String) {
        withContext(Dispatchers.IO) {
            // Delete the existing video if it exists
            deleteCityVideoUri(context)

            // Create a new video entry
            val newCityVideoUri = createCityVideoUri(context)

            // Download and save the video
            if (newCityVideoUri != null) {
                try {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(videoUrl).build()
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        val inputStream = response.body?.byteStream()

                        context.contentResolver.openFileDescriptor(newCityVideoUri, "w")
                            ?.use { fileDescriptor ->
                                FileOutputStream(fileDescriptor.fileDescriptor).use { outputStream ->
                                    inputStream?.copyTo(outputStream)
                                    inputStream?.close()
                                    outputStream.flush()
                                }
                            }

                        Log.d("VideoStorageUtil", "Video downloaded and saved successfully")
                    } else {
                        throw IOException("Failed to download video")
                    }
                } catch (e: Exception) {
                    Log.e("VideoStorageUtil", "Error downloading video", e)
                }
            }
        }
    }

    // we call this many times because there's same uri all city videos so if we don't delete any old video it will load other city's video
    fun deleteCityVideoUri(context: Context) {
        val cityVideoUri = getCityVideoUri(context)
        if (cityVideoUri != null) {
            context.contentResolver.delete(cityVideoUri, null, null)
            Log.e("Video Status", "Deleted")
        }
    }

    private fun createCityVideoUri(context: Context): Uri? {
        val contentResolver = context.contentResolver
        val videoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, CITY_VIDEO_FILENAME)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        }

        return contentResolver.insert(videoCollection, contentValues)
    }

    fun getCityVideoUri(context: Context): Uri? {
        val contentResolver = context.contentResolver
        val videoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(MediaStore.Video.Media._ID)
        val selection = "${MediaStore.Video.Media.DISPLAY_NAME}=?"
        val selectionArgs = arrayOf(CITY_VIDEO_FILENAME)

        return contentResolver.query(videoCollection, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val id = cursor.getLong(idColumn)
                    Uri.withAppendedPath(videoCollection, id.toString())
                } else {
                    null
                }
            }
    }

}