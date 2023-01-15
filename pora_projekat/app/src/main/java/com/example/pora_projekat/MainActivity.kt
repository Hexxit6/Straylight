package com.example.pora_projekat

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pora_projekat.services.AudioService
import com.example.pora_projekat.services.AudioServiceSimulation
import com.example.pora_projekat.services.ImageService


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: delete
        // Example of starting the service

        // Intent(this, ImageService::class.java).also { intent ->
        //     intent.putExtra(ImageService.EXTRA_FROM, 0f)
        //     intent.putExtra(ImageService.EXTRA_TO, 24f)
        //     intent.putExtra(ImageService.EXTRA_INTERVAL, 10)
        //     intent.putExtra(ImageService.EXTRA_LATITUDE, 0f)
        //     intent.putExtra(ImageService.EXTRA_LONGITUDE, 0f)
        //     startService(intent)
        // }

        // Intent(this, AudioService::class.java).also { intent ->
        //     intent.putExtra(AudioService.EXTRA_FROM, 0f)
        //     intent.putExtra(AudioService.EXTRA_TO, 24f)
        //     intent.putExtra(AudioService.EXTRA_INTERVAL, 30)
        //     intent.putExtra(AudioService.EXTRA_DURATION, 5)
        //     intent.putExtra(AudioService.EXTRA_LATITUDE, 0f)
        //     intent.putExtra(AudioService.EXTRA_LONGITUDE, 0f)
        //     startService(intent)
        // }

        // Intent(this, AudioServiceSimulation::class.java).also { intent ->
        //     intent.putExtra(AudioService.EXTRA_FROM, 0f)
        //     intent.putExtra(AudioService.EXTRA_TO, 24f)
        //     intent.putExtra(AudioService.EXTRA_INTERVAL, 30)
        //     intent.putExtra(AudioService.EXTRA_DURATION, 5)
        //     intent.putExtra(AudioService.EXTRA_LATITUDE, 0f)
        //     intent.putExtra(AudioService.EXTRA_LONGITUDE, 0f)
        //     startService(intent)
        // }
    }
}