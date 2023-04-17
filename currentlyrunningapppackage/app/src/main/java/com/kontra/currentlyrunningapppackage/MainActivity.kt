package com.kontra.currentlyrunningapppackage

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.kontra.currentlyrunningapppackage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if(Utils.isServiceStarted(this)){
            Utils.startService(this)
            binding.serviceStatus.text = getString(R.string.label_service_started)
            binding.serviceHandler.text = getString(R.string.label_stop_service)
        }
        binding.serviceHandler.setOnClickListener {
            if(Utils.isServiceStarted(this)){
                Utils.stopService(this)
                binding.serviceStatus.text = getString(R.string.label_service_stopped)
                binding.serviceHandler.text = getString(R.string.label_start_service)
            }
            else{
                Utils.startService(this)
                binding.serviceStatus.text = getString(R.string.label_service_started)
                binding.serviceHandler.text = getString(R.string.label_stop_service)
            }
        }

        setContentView(binding.root)
        Utils.checkOverlayPermission(this)
    }

//    override fun onPause() {
//        super.onPause()
//        if(Utils.isServiceStarted(this)){
//            Window.getInstance(this).changePackageName()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if(Utils.isServiceStarted(this)){
//            Window.getInstance(this).changePackageName()
//        }
//    }

}