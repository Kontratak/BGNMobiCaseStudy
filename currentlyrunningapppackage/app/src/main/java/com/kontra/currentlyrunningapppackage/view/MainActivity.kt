package com.kontra.currentlyrunningapppackage.view

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.kontra.currentlyrunningapppackage.R
import com.kontra.currentlyrunningapppackage.databinding.ActivityMainBinding
import com.kontra.currentlyrunningapppackage.model.enums.ServiceStatus
import com.kontra.currentlyrunningapppackage.viewmodels.ServiceViewModel
import com.rvalerio.fgchecker.Utils.hasUsageStatsPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val serviceViewModel by viewModels<ServiceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        //if service started is true it starts the service and changes information on the app according
        if(serviceViewModel.isServiceStarted(this)){
            binding.serviceStatus.text = getString(R.string.label_service_started)
            binding.serviceHandler.text = getString(R.string.label_stop_service)
            serviceViewModel.startForegroundService(this)
        }

        serviceViewModel.serviceStatus.observe(this, Observer {
            if(it == ServiceStatus.STARTED){
                binding.serviceStatus.text = getString(R.string.label_service_started)
                binding.serviceHandler.text = getString(R.string.label_stop_service)
            }
            else{
                binding.serviceStatus.text = getString(R.string.label_service_stopped)
                binding.serviceHandler.text = getString(R.string.label_start_service)
            }
        })

        binding.serviceHandler.setOnClickListener {
            serviceViewModel.checkPermissions(this)
            if(serviceViewModel.hasPermissions(this)){
                if(serviceViewModel.serviceStatus.value == ServiceStatus.STARTED){
                    serviceViewModel.stopForegroundService(this)
                    binding.serviceStatus.text = getString(R.string.label_service_stopped)
                    binding.serviceHandler.text = getString(R.string.label_start_service)
                }
                else{
                    serviceViewModel.startForegroundService(this)
                    binding.serviceStatus.text = getString(R.string.label_service_started)
                    binding.serviceHandler.text = getString(R.string.label_stop_service)
                }
            }

        }

        setContentView(binding.root)
        serviceViewModel.checkPermissions(this)
    }

}