package com.ssafy.popcon.gallery

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ssafy.popcon.ui.common.MainActivity
import kotlinx.coroutines.*

private const val TAG = "GalleryJobService"
class GalleryJobService: JobService() {
    private var galleryReceiver = GalleryReceiver()
    private lateinit var params: JobParameters
    private lateinit var mainActivity: MainActivity

    override fun onCreate() {
        super.onCreate()
        mainActivity = MainActivity.getInstance()!!
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartJob(_params: JobParameters?): Boolean {
        params = _params!!
        val addGalleryGifticon = AddGalleryGifticon(mainActivity, applicationContext, contentResolver)
        addGalleryGifticon.getImgList()

        //doBackgroundWork()
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
                intentFilter.addAction(
                    "android.provider.MediaStore.Images"
                )
                intentFilter.addDataType(
                    "application/image/jpeg"
                )
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