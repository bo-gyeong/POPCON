package com.ssafy.popcon

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.kakao.sdk.common.util.Utility
import com.ssafy.popcon.config.WearApplicationClass
import com.ssafy.popcon.databinding.ActivityMainBinding
import com.ssafy.popcon.ui.login.LoginActivity
import com.ssafy.popcon.ui.map.DonateActivity
import com.ssafy.popcon.util.CheckPermission
import com.ssafy.popcon.util.SharedPreferencesUtil

private const val TAG = "MainActivity_싸피"

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var checkPermission: CheckPermission
    private var permissionGranted = false

    val PERMISSION_REQUEST_CODE = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WearApplicationClass.makeRetrofit(WearApplicationClass.SERVER_URL)

        //자동로그인
        if (SharedPreferencesUtil(this).getUser().email != "") {
            Log.d(TAG, "onCreate: 로그인됨")
            val intent = Intent(this, DonateActivity::class.java)
            //val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
            finish()
        } else {
            Log.d(TAG, "onCreate: 로그인 필요")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private val runtimePermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    // 위치 권한
    fun checkPermissions() {
        checkPermission = CheckPermission(this)

        if (!checkPermission.runtimeCheckPermission(this, *runtimePermissions)) {
            ActivityCompat.requestPermissions(this, runtimePermissions, PERMISSION_REQUEST_CODE)
        }
    }

    //권한 요청
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    //권한 승인
                    permissionGranted = true
                } else {
                    checkPermission.requestPermission()
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        checkPermissions()
    }
}