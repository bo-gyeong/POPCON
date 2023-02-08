package com.ssafy.popcon.ui.add

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.IBinder
/**  https://velog.io/@alsgk721/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-18-Background-Limit  **/
class MyService : JobService() {
    override fun onStartJob(p0: JobParameters?): Boolean {
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }
}