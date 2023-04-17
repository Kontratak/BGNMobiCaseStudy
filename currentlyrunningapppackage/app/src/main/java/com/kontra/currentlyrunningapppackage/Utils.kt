package com.kontra.currentlyrunningapppackage

import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings


class Utils {

    companion object{

        fun startService(context: Context) {
            val sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences_key),Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putBoolean(context.getString(R.string.service_started), true)
                commit()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // check if the user has already granted
                // the Draw over other apps permission
                if (Settings.canDrawOverlays(context)) {
                    // start the service based on the android version
                    context.startService(Intent(context, ForegroundService::class.java))
                }
            } else {
                context.startService(Intent(context, ForegroundService::class.java))
            }
        }

        fun currentAppPackageNameOnForeground(
            context: Context
        ): String? {
            try {
                val activityManager =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

                val tasks: List<RunningTaskInfo> = activityManager
                    .getRunningTasks(1)
                return if (tasks.isNotEmpty()) {
                    tasks[0].topActivity?.packageName
                } else ""
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return ""
        }

        fun isServiceStarted(context: Context?): Boolean {
            if (context == null) return false;
            return try {
                val sharedPref = context?.getSharedPreferences(
                    context.getString(R.string.shared_preferences_key),
                    Context.MODE_PRIVATE
                )
                sharedPref.getBoolean(context.getString(R.string.service_started), false);
            } catch (e : java.lang.Exception){
                false;
            }
        }

        fun stopService(context: Context) {
            val sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences_key),Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putBoolean(context.getString(R.string.service_started), false)
                commit()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // check if the user has already granted
                // the Draw over other apps permission
                if (Settings.canDrawOverlays(context)) {
                    // start the service based on the android version
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.stopService(Intent(context, ForegroundService::class.java))
                    } else {
                        context.stopService(Intent(context, ForegroundService::class.java))
                    }
                }
            } else {
                context.stopService(Intent(context, ForegroundService::class.java))
            }
        }

        // method to ask user to grant the Overlay permission
        fun checkOverlayPermission(context : Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    // send user to the device settings
                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    context.startActivity(myIntent)
                }
            }
        }
    }

}