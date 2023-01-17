package com.example.pora_projekat

import android.app.Application
import com.projekt.lib.MyEvent
import com.projekt.lib.MyLocation
import java.time.LocalDateTime

class MyApplication : Application() {
    lateinit var theme: String
    lateinit var event: MyEvent

    override fun onCreate() {
        super.onCreate()
        theme = ""
        event = MyEvent("", MyLocation(null, null))
    }

}
