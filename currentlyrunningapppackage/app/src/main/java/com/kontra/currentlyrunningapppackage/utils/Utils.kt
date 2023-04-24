package com.kontra.currentlyrunningapppackage.utils

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import com.kontra.currentlyrunningapppackage.R
import com.kontra.currentlyrunningapppackage.model.enums.ServiceStatus
import com.kontra.currentlyrunningapppackage.services.ForegroundService
import com.rvalerio.fgchecker.AppChecker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Utils @Inject constructor() {

    var onServiceStatusChanged : ((value : ServiceStatus) -> Unit)? = null
    var onPackageNameChanged : ((value : String) -> Unit)? = null


    //this method starts foreground service for in order to window to function
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
        onServiceStatusChanged?.let { it(ServiceStatus.STARTED) }
        currentAppPackageNameOnForeground(context)
    }

    //this method stops the service and calls viewModel listener to perform some stuff
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
        onServiceStatusChanged?.let { it(ServiceStatus.STOPPED) }
    }

    //appChecker package handles foreground app changes on 500ms intervals
    private fun currentAppPackageNameOnForeground(context: Context){
        var appChecker = AppChecker();
        appChecker
            .whenAny(AppChecker.Listener() {
                onPackageNameChanged?.let { it1 -> it1(it) }
            }).timeout(500).start(context);
    }

    //this checks preferences if service is started earlier
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



    // method to ask user to grant the Overlay and usage stats permission
    fun checkPermissions(context : Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                // send user to the device settings
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                context.startActivity(myIntent)
            }
        }
        requestUsageStatsPermission(context)
    }

    private fun requestUsageStatsPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            && !hasUsageStatsPermission(context)
        ) {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps =
            context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            "android:get_usage_stats",
            Process.myUid(), context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

}