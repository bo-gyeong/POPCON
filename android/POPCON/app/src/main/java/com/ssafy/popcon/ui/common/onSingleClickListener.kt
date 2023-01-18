package com.ssafy.popcon.ui.common

import android.os.SystemClock
import android.view.View

abstract class onSingleClickListener: View.OnClickListener {
    private val MIN_CLICK_INTERVAL = 1000
    private var mLastClickTime:Long = 0

    abstract fun onSingleClick(v:View)

    override fun onClick(p0: View?) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime

        if (elapsedTime > MIN_CLICK_INTERVAL){
            onSingleClick(p0!!)
        }
    }
}