package com.kontra.currentlyrunningapppackage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StartupBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (action != null) {
            if (action == Intent.ACTION_BOOT_COMPLETED) {
                if (context != null) {
                    if(Utils.isServiceStarted(context))
                        Utils.startService(context)
                }
            }
        }
    }

}