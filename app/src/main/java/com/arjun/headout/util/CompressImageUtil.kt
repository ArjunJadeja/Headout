package com.arjun.headout.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CompressImageUtil {
    @Throws(IOException::class)
    fun compressImage(context: Context, imageUri: Uri, quality: Int): Uri {
        // Get the original bitmap from the Uri
        val original = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))

        // Create a unique file name by appending the current timestamp and a random number
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val randomNumber = (1..1_000_000).random()
        val fileName = "compressed_${timeStamp}_$randomNumber.jpg"

        // Create an output stream to a new file where the compressed image will be saved
        val compressedFile = File(context.cacheDir, fileName)
        val out = FileOutputStream(compressedFile)

        // Compress the original bitmap into the output stream created
        original.compress(Bitmap.CompressFormat.JPEG, quality, out)
        out.close()

        // Return the Uri of the compressed image file
        return Uri.fromFile(compressedFile)
    }
}