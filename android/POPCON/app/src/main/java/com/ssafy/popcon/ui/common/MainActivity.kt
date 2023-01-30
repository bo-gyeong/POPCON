package com.ssafy.popcon.ui.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ActivityMainBinding
import com.ssafy.popcon.ui.add.AddFragment
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.ui.login.LoginFragment
import com.ssafy.popcon.ui.map.MapFragment
import com.ssafy.popcon.ui.settings.SettingsFragment
import com.ssafy.popcon.util.CheckPermission
import com.ssafy.popcon.util.ShakeDetector
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.util.Utils.navigationHeight
import com.ssafy.popcon.util.Utils.setStatusBarTransparent
import com.ssafy.popcon.viewmodel.FCMViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

private const val TAG = "MainActivity_싸피"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var checkPermission: CheckPermission
    private var permissionGranted = false

    private val fcmViewModel: FCMViewModel by viewModels { ViewModelFactory(this) }

    val PERMISSION_REQUEST_CODE = 8

    init {
        instance = this
    }

    companion object {
        var shakeDetector = ShakeDetector()
        const val channel_id = "popcon_user"

        private var instance: MainActivity? = null
        fun getInstance(): MainActivity? {
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavBar()
        checkPermissions()
        //getFCMToken()
        
        //자동로그인
        if (SharedPreferencesUtil(this).getUser().email != "") {
            Log.d(TAG, "onCreate: 로그인됨")
            changeFragment(HomeFragment())
        } else {
            Log.d(TAG, "onCreate: 로그인 필요")
            changeFragment(LoginFragment())
        }
    }

    //navigation bar 설정
    private fun setNavBar() {
        //this.setStatusBarTransparent() // 투명 상태 바

        window.navigationBarColor = Color.WHITE;

        val radius = resources.getDimension(R.dimen.radius_small)
        val bottomNavigationViewBackground = binding.bottomNav.background as MaterialShapeDrawable
        bottomNavigationViewBackground.shapeAppearanceModel =
            bottomNavigationViewBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                .build()

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    changeFragment(HomeFragment())
                    true
                }
                R.id.addFragment -> {
                    addFragment(AddFragment())
                    true
                }
                R.id.mapFragment ->{
                    changeFragment(MapFragment())
                    true
                }
                R.id.settingsFragment->{
                    changeFragment(SettingsFragment())
                    true
                }
                //donateFragment 추가하기
                else -> false
            }
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout_main, fragment)
            .commit()
    }

    fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout_main, fragment)
            .addToBackStack(null)
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
            binding.bottomNav.visibility = View.GONE
        } else {
            binding.bottomNav.visibility = View.VISIBLE
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

    // 토큰 보내기
    fun uploadToken(token: String) {
        fcmViewModel.uploadToken(token)
    }

    // 알림 관련 메시지 전송
    fun sendMessageTo(token: String, title: String, body: String) {
        fcmViewModel.sendMessageTo(token, title, body)
        //mainActivity.sendMessageTo(fcmViewModel.token, "title", "texttttttbody") 이렇게 호출
    }

    // 토큰 생성
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            Log.d(TAG, "token 정보: ${task.result ?: "task.result is null"}")
            if (task.result != null) {
                uploadToken(task.result)
                fcmViewModel.setToken(task.result)
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        checkPermissions()
    }
}