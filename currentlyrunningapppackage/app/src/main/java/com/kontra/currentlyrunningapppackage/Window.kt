package com.kontra.currentlyrunningapppackage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.*
import com.kontra.currentlyrunningapppackage.databinding.ActivityMainBinding
import com.kontra.currentlyrunningapppackage.databinding.PopupWindowBinding

class Window(  // declaring required variables
    private val context: Context
) {
    companion object{

       @SuppressLint("StaticFieldLeak")
       var windowInstance : Window? = null

        fun getInstance(context: Context) : Window{
            if(windowInstance == null)
                windowInstance = Window(context)
            return windowInstance!!
        }
    }

    private var mParams: WindowManager.LayoutParams? = null
    private val mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater
    private var binding: PopupWindowBinding
    fun open() {
        try {
            // check if the view is already
            // inflated or present in the window
            if (binding.root.windowToken == null) {
                if (binding.root.parent == null) {
                    mWindowManager.addView(binding.root, mParams)
                }
            }
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

    fun close() {
        try {
            // remove the view from the window
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(binding.root)
            // invalidate the view
            binding.root.invalidate()
            // remove all views
            (binding.root as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = WindowManager.LayoutParams( // Shrink the window to wrap the content rather
                // than filling the screen
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,  // Display it on top of other application windows
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Don't let it grab the input focus
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // Make the underlying application window visible
                // through any transparent parts
                PixelFormat.TRANSLUCENT
            )
        }
        // getting a LayoutInflater
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = PopupWindowBinding.inflate(layoutInflater)
        var appName = Utils.currentAppPackageNameOnForeground(context)
        binding.titleText.text = appName ?: ""

        // set onClickListener on the remove button, which removes
        binding.windowClose.setOnClickListener{
            close()
        }
        // the view from the window
        // Define the position of the
        // window within the screen
        mParams!!.gravity = Gravity.CENTER
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun changePackageName(){
        var appName = Utils.currentAppPackageNameOnForeground(context)
        binding.titleText.text = appName ?: ""
    }
}