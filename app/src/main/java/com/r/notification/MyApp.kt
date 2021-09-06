package com.r.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import java.util.*

class MyApp : Application() {

    companion object {
        const val CHANNEL_1_ID = "channel1"
        const val CHANNEL_2_ID = "channel2"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_1_ID,
                "I am here",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = Calendar.getInstance().time.toString()
            /*  val channel2 = NotificationChannel(
                  CHANNEL_2_ID,
                  "Channel 2",
                  NotificationManager.IMPORTANCE_LOW
              )
              channel2.description = "This is Channel 2"*/
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)
//            manager.createNotificationChannel(channel2)
        }
    }
}