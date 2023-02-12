package com.ssafy.popcon.gallery

import android.app.AlertDialog
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ssafy.popcon.R
import com.ssafy.popcon.mms.MMSDialog
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.viewmodel.AddViewModel
import kotlinx.coroutines.*

private const val TAG = "GalleryJobService"
class GalleryJobService: JobService() {
    private var galleryReceiver = GalleryReceiver()
    private lateinit var params: JobParameters
    private lateinit var mainActivity: MainActivity

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartJob(_params: JobParameters?): Boolean {
        params = _params!!
        mainActivity = MainActivity.getInstance()!!

//        val addGalleryGifticon = AddGalleryGifticon(
//            mainActivity, applicationContext, contentResolver, true
//        )
//        addGalleryGifticon.getImgList()

        doBackgroundWork()
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun doBackgroundWork(){
        GlobalScope.launch {
            withContext(Dispatchers.Default){
                val intentFilter = IntentFilter()
//                intentFilter.addAction(
//                    Intent.ACTION_MEDIA_SCANNER_STARTED
//                )
//                intentFilter.addDataType(
//                    "image/*"
//                )
                registerReceiver(galleryReceiver, intentFilter)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try{
            unregisterReceiver(galleryReceiver)
            jobFinished(params, true)
        }catch (e: java.lang.Exception){
            Log.e(TAG, "onDestroy: GalleryJobService unregisterReceiver failed")
        }
    }
}