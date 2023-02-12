package com.ssafy.popcon.mms

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.IntentFilter
import android.util.Log
import com.ssafy.popcon.ui.common.MainActivity
import kotlinx.coroutines.*

private const val TAG = "MyService"
class MMSJobService : JobService() {
    private var mmsReceiver = MMSReceiver()
    private lateinit var params: JobParameters

    override fun onStartJob(p0: JobParameters?): Boolean {
        params = p0!!
        doBackgroundWork()
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun doBackgroundWork(){
        GlobalScope.launch {
            withContext(Dispatchers.Default) {
                val intentFilter = IntentFilter()
                intentFilter.addAction(
                    "android.provider.Telephony.WAP_PUSH_RECEIVED"
                )
                intentFilter.addDataType(
                    "application/vnd.wap.mms-message"
                )
                registerReceiver(mmsReceiver, intentFilter)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(mmsReceiver)
            jobFinished(params, true)
        } catch (e: java.lang.Exception){
            Log.e(TAG, "onDestroy: jobService unregisterReceiver failed")
        }
    }
}