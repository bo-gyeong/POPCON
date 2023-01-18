package com.ssafy.popcon.ui.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
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
import com.ssafy.popcon.util.Utils.navigationHeight
import com.ssafy.popcon.util.Utils.setStatusBarTransparent

private const val TAG = "MainActivity 메인"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var checkPermission: CheckPermission
    private var permissionGranted = false

    val PERMISSION_REQUEST_CODE = 8

    companion object {
        var shakeDetector = ShakeDetector()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavBar()
        checkPermissions()
    }

    //navigation bar 설정
    private fun setNavBar() {
        this.setStatusBarTransparent() // 투명 상태 바
        binding.lBottomNavigationView.setPadding(
            0,
            0,
            0,
            this.navigationHeight()
        )
        val navHosFragment =
            supportFragmentManager.findFragmentById(R.id.frame_layout_main) as NavHostFragment
        val navController = navHosFragment.navController

        NavigationUI.setupWithNavController(binding.lBottomNavigationView, navController)

        // 재선택시 다시 렌더링 하지 않기 위해 수정
        binding.lBottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.addFragment -> {
                    if (binding.lBottomNavigationView.selectedItemId == R.id.homeFragment) {
                        navController.navigate(R.id.action_homeFragment_to_addFragment)
                    } else if (binding.lBottomNavigationView.selectedItemId == R.id.mapFragment) {
                        navController.navigate(R.id.action_mapFragment_to_addFragment)
                    }
                }
                R.id.homeFragment -> {
                    if (binding.lBottomNavigationView.selectedItemId != R.id.homeFragment)
                        changeFragment(HomeFragment())
                }
                R.id.mapFragment -> {
                    if (binding.lBottomNavigationView.selectedItemId != R.id.mapFragment)
                        changeFragment(MapFragment())
                }
            }
            true
        }

        binding.btnFab.setOnClickListener{
            changeFragment(AddFragment())
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout_main, fragment)
            .commit()
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

    //하단바 숨기기
    fun hideBottomNav(state: Boolean) {
        if (state) {
            binding.lBottomNavigationView.visibility = View.GONE
            binding.lFabContainer.visibility = View.GONE
        } else {
            binding.lBottomNavigationView.visibility = View.VISIBLE
            binding.lFabContainer.visibility = View.VISIBLE
        }
    }

    //앱이 실행중 아닐때 흔들기 제거
    override fun onPause() {
        removeShakeSensor(this)
        super.onPause()
    }

    //흔들기 설정
    fun setShakeSensor(context: Context, shakeDetector: ShakeDetector) {//센서
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    //흔들기 제거
    fun removeShakeSensor(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(shakeDetector)

        super.onPause()
    }

    override fun onRestart() {
        super.onRestart()
        checkPermissions()
    }
}