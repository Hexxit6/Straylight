package com.example.pora_projekat.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.pora_projekat.R
import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask

class AudioService : Service() {

    private var fromTime: Float? = 0f       // TODO: change
    private var toTime: Float? = 24f        // TODO: change
    private var interval: Int? = 60000
    private var duration: Int? = 10000
    private var latitude: Float? = 0f
    private var longitude: Float? = 0f

    private var firstRun = true
    private var mediaRecorder: MediaRecorder? = null
    private var recording = false
    private var timer: Timer? = null
    private var filepath: String = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (firstRun) {
            firstRun = false

            fromTime = intent?.getFloatExtra(EXTRA_FROM, 0f)
            toTime = intent?.getFloatExtra(EXTRA_TO, 24f)
            interval = intent?.getIntExtra(EXTRA_INTERVAL, 60)!!.times(1000)
            duration = intent?.getIntExtra(EXTRA_DURATION, 10)!!.times(1000)
            latitude = intent?.getFloatExtra(EXTRA_LATITUDE, 0f)
            longitude = intent?.getFloatExtra(EXTRA_LONGITUDE, 0f)

            startRecording()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startRecording() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.icons8_record_30)
            .setContentTitle(CHANNEL_NAME)

        Thread {
            while (true) {
                if (!recording) {
                    try {
                        filepath = getOutputFileName()
                        mediaRecorder = MediaRecorder()
                        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        mediaRecorder?.setOutputFile(filepath)
                        mediaRecorder?.prepare()
                        mediaRecorder?.start()
                        recording = true

                        timer = Timer()
                        timer?.schedule(object : TimerTask() {
                            override fun run() {
                                stopRecording()
                            }
                        }, duration!!.toLong())
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                Thread.sleep(interval!!.toLong())
            }
        }.start()

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("LogNotTimber")
    private fun getOutputFileName(): String {
        val file = File(filesDir.path, "${System.currentTimeMillis()}.3gp")
        Log.d("AudioService", "Output path: ${file.path}")
        return file.path
    }

    private fun stopRecording() {
        if (recording) {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null
            recording = false
            timer?.cancel()
            timer = null
            APIUtil.uploadFile(filepath, APIUtil.BASE_URL, longitude.toString(), latitude.toString(), APIUtil.MIME_MP3)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "audio_service_channel"
        private const val CHANNEL_NAME = "Audio Service"
        private const val NOTIFICATION_ID = 1

        const val EXTRA_FROM = "from"
        const val EXTRA_TO = "to"
        const val EXTRA_INTERVAL = "interval"
        const val EXTRA_DURATION = "duration"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
    }
}
