package com.example.background.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "SaveImageToFileWorker"
class SaveImageToFileWorker(appContext: Context, workerParameters: WorkerParameters):
    Worker(appContext, workerParameters) {

    private val title = "Blurred Image"
    private val dateFormatter = SimpleDateFormat(
        "yyyy.MM.dd 'at' HH:mm:ss z",
        Locale.getDefault()
    )
    override fun doWork(): Result {
        makeStatusNotification("Saving Image", applicationContext)
        sleep()

        val resolver = applicationContext.contentResolver
        return try {
            val resourceUri = inputData.getString(KEY_IMAGE_URI)
            val bitmap = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )
            val imageUrL = MediaStore.Images.Media.insertImage(
                resolver, bitmap, title, dateFormatter.format(Date())
            )
            if (!imageUrL.isNullOrEmpty()) {
                val output = workDataOf(KEY_IMAGE_URI to imageUrL)
                Result.success(output)
            } else{
                Log.e(TAG, "Writing to MediaStore failed")
                Result.failure()
            }
        } catch (exception:Exception) {
            exception.printStackTrace()
            Result.failure()
        }

    }
}