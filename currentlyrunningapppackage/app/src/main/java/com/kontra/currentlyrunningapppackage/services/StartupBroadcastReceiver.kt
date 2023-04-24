package com.kontra.currentlyrunningapppackage.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kontra.currentlyrunningapppackage.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint(BroadcastReceiver::class)
class StartupBroadcastReceiver @Inject constructor() : Hilt_StartupBroadcastReceiver() {

    @Inject
    lateinit var utils: Utils

    //Broadcast receiver helps us to catch android events in this case we used it for phone restarting event
    override fun onReceive(context: Context?, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        if (action != null) {
            if (action == Intent.ACTION_BOOT_COMPLETED) {
                if (context != null) {
                    if(utils.isServiceStarted(context))
                        utils.startService(context)
                }
            }
        }
    }

}