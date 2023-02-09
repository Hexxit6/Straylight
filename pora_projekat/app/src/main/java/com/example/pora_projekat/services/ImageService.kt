package com.example.pora_projekat.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.pora_projekat.R

class ImageService : Service() {

    var fromTime: Float? = 0f       // TODO: change
    var toTime: Float? = 24f        // TODO: change
    var interval: Int? = 60000    // in seconds (default = 10min)
    var latitude: Float? = 0f
    var longitude: Float? = 0f

    var firstRun = true
    // val camera = ImageCapture.Builder().build()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (firstRun) {
            firstRun = false

            fromTime = intent?.getFloatExtra(EXTRA_FROM, 0f)
            toTime = intent?.getFloatExtra(EXTRA_TO, 24f)
            interval = intent?.getIntExtra(EXTRA_INTERVAL, 5)!!.times(1000)     // TODO: change
            latitude = intent?.getFloatExtra(EXTRA_LATITUDE, 0f)
            longitude = intent?.getFloatExtra(EXTRA_LONGITUDE, 0f)

            startForegroundService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("LogNotTimber")
    @RequiresApi(Build.VERSION_CODES.P)
    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.icons8_compact_camera_50)
            .setContentTitle(CHANNEL_NAME)

        Thread {
            while (true) {
                Log.d("ImageService", "Thread is executing...")

                // val imageFile = createTempFile("image", ".jpg")
                // val outputFileOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()
                // camera.takePicture(outputFileOptions, mainExecutor, object: ImageCapture.OnImageSavedCallback {
                //     override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                //         Log.d("ImageService", "Image was successfully saved to ${imageFile.name}")
                //     }
                //     override fun onError(exception: ImageCaptureException) {
                //         val errorType = exception.imageCaptureError
                //         Log.d("ImageService", "Error in takePicture - error type: $errorType")
                //         Log.d("ImageService", "${exception.message}")
                //     }
                // })

                Thread.sleep(interval!!.toLong())
            }
        }.start()

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(p0: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "image_service_channel"
        const val CHANNEL_NAME = "Image Service"
        const val NOTIFICATION_ID = 1

        const val EXTRA_FROM = "from"
        const val EXTRA_TO = "to"
        const val EXTRA_INTERVAL = "interval"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
    }
}
