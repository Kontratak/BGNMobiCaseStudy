package com.kontra.currentlyrunningapppackage.viewmodels

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontra.currentlyrunningapppackage.model.enums.ServiceStatus
import com.kontra.currentlyrunningapppackage.utils.Utils
import com.kontra.currentlyrunningapppackage.view.Window
import com.rvalerio.fgchecker.Utils.hasUsageStatsPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(private val utils: Utils,private val application: Application) : ViewModel() {

    private var _serviceStatus = MutableLiveData(ServiceStatus.STOPPED)
    val serviceStatus : LiveData<ServiceStatus> get() = _serviceStatus

    var window : Window = Window(utils, application.baseContext){
        _serviceStatus.value = ServiceStatus.STOPPED
    }


    init {
        utils.onServiceStatusChanged = {
            _serviceStatus.value = it
            if(it == ServiceStatus.STARTED)
                window.open()
            else window.close()
        }
        utils.onPackageNameChanged = {
            window.changePackageName(it)
        }
    }

    fun startForegroundService(context: Context) {
        utils.startService(context)
        window.open()
    }

    fun stopForegroundService(context: Context) {
        utils.stopService(context)
        window.close()
    }

    fun checkPermissions(context: Context) {
        utils.checkPermissions(context)
    }

    fun isServiceStarted(context: Context): Boolean {
        return utils.isServiceStarted(context)
    }

    fun hasPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasUsageStatsPermission(context) && Settings.canDrawOverlays(context)
        } else {
            return false
        }
    }


}