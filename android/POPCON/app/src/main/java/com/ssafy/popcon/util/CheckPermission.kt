package com.ssafy.popcon.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.ActivityCompat

class CheckPermission(private val context: Context) {
    fun runtimeCheckPermission(context: Context?, vararg permissions: String?): Boolean{
        if (context != null && permissions != null){
            for (permission in permissions){
                if (ActivityCompat.checkSelfPermission(context, permission!!)
                    != PackageManager.PERMISSION_GRANTED
                ){
                    return false
                }
            }
        }
        return true
    }

    fun requestPermission(){
        val alert = AlertDialog.Builder(context)

        alert.setTitle("권한이 필요합니다.")
        alert.setMessage("설정으로 이동합니다.")
        alert.setCancelable(false)

        alert.setPositiveButton("확인"){ dialogInterface, i ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + context.packageName))

            context.startActivity(intent)
            dialogInterface.cancel()
        }
        alert.setNegativeButton("취소"){ dialogInterface, i ->
            alert.show()
        }
        alert.show()
    }
}