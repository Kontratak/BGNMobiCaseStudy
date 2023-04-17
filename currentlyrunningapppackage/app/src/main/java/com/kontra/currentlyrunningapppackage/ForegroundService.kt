package com.kontra.currentlyrunningapppackage

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class ForegroundService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private lateinit var window: Window


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        window = Window.getInstance(this)
        window.open()
        checkActivity(this)
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        window.close()
        super.onDestroy()
    }

    var handler = Handler()

    fun checkActivity(context: Context) {
        var activityRunnable = ActivityRunnable(handler,context)
        handler.postDelayed(activityRunnable, 500)
    }

    private class ActivityRunnable(var handler: Handler,var context: Context) : Runnable {
        override fun run() {
            if(Utils.isServiceStarted(context)){
                Window.getInstance(context).changePackageName()
            }
            handler.postDelayed(this, 500)
        }
    }

    // for android version >=O we need to create
    // custom notification stating
    // foreground service is running
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val channelName = "Background Service"
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_MIN
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Service running")
            .setContentText("Displaying over other apps") // this is important, otherwise the notification will show the way
            // you want i.e. it will show some default notification
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }
}

