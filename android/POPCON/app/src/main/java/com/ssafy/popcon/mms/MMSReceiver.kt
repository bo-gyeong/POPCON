package com.ssafy.popcon.mms

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.widget.Toast

@SuppressLint("Range")
class MMSReceiver: BroadcastReceiver() {
    private lateinit var contentResolver: ContentResolver
    private lateinit var mmsData: MMSData

    override fun onReceive(_context: Context?, _intent: Intent?) {
        contentResolver = _context!!.contentResolver
        mmsData = MMSData(_context.contentResolver, _context, false)
        mmsData.chkMMS()
    }
}