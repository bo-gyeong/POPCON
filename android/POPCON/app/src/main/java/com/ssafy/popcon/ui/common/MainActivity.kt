package com.ssafy.popcon.ui.common

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ActivityMainBinding
import com.ssafy.popcon.ui.add.AddFragment
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.ui.map.MapFragment
import com.ssafy.popcon.util.CheckPermission
import com.ssafy.popcon.util.ShakeDetector
import com.ssafy.popcon.util.ShakeDetector.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var checkPermission: CheckPermission
    private var permissionGranted = false

    val PERMISSION_REQUEST_CODE = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHosFragment =
            supportFragmentManager.findFragmentById(R.id.frame_layout_main) as NavHostFragment
        val navController = navHosFragment.navController

        NavigationUI.setupWithNavController(binding.tabLayoutBottomNavigation, navController)
        binding.tabLayoutBottomNavigation.setOnNavigationItemReselectedListener { item ->
            // 재선택시 다시 랜더링 하지 않기 위해 수정
            if (binding.tabLayoutBottomNavigation.selectedItemId != item.itemId) {
                binding.tabLayoutBottomNavigation.selectedItemId = item.itemId
            }
        }
        checkPermissions()
    }

    fun setShakeSensor(context: Context) {//센서
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector()
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)

        shakeDetector.setOnShakeListener(object : OnShakeListener {
            override fun onShake(count: Int) {
                Toast.makeText(context, "Shake", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 프래그먼트 바꾸기
    fun changeFragment(idx:Int){
        when(idx){
            //home으로 이동
            0 -> {
                binding.tabLayoutBottomNavigation.selectedItemId = R.id.homeFragment
            }
            //add로 이동
            1 -> {
                binding.tabLayoutBottomNavigation.selectedItemId = R.id.addFragment
            }
            //map으로 이동
            2 -> {
                binding.tabLayoutBottomNavigation.selectedItemId = R.id.mapFragment
            }
        }
    }

    private val runtimePermissions = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    // 위치, 갤러리, 전화 권한
    private fun checkPermissions() {
        checkPermission = CheckPermission(this)

        if (!checkPermission.runtimeCheckPermission(this, *runtimePermissions)) {
            ActivityCompat.requestPermissions(this, runtimePermissions, PERMISSION_REQUEST_CODE)
        }
    }

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
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                ) {
                    //권한 승인
                    permissionGranted = true
                } else {
                    checkPermission.requestPermission()
                }
            }
        }
    }

    fun hideBottomNav(state: Boolean) {
        if (state) binding.tabLayoutBottomNavigation.visibility = View.GONE
        else binding.tabLayoutBottomNavigation.visibility = View.VISIBLE
    }

    override fun onPause() {
//        sensorManager.unregisterListener(shakeDetector)
        super.onPause()
    }

    override fun onRestart() {
        super.onRestart()
        checkPermissions()
    }
}