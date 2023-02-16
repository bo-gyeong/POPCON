package com.ssafy.popcon.ui.common

import android.Manifest
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.wearable.*
import com.google.android.gms.wearable.Wearable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.kakao.sdk.common.util.Utility
import com.ssafy.popcon.R
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.databinding.ActivityMainBinding
import com.ssafy.popcon.gallery.AddGalleryGifticon
import com.ssafy.popcon.ui.add.AddFragment
import com.ssafy.popcon.mms.MMSDialog
import com.ssafy.popcon.mms.MMSJobService
import com.ssafy.popcon.repository.fcm.FCMRemoteDataSource
import com.ssafy.popcon.repository.fcm.FCMRepository
import com.ssafy.popcon.ui.add.*
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.ui.login.LoginFragment
import com.ssafy.popcon.ui.map.MapFragment
import com.ssafy.popcon.ui.settings.SettingsFragment
import com.ssafy.popcon.util.CheckPermission
import com.ssafy.popcon.util.RetrofitUtil
import com.ssafy.popcon.util.ShakeDetector
import com.ssafy.popcon.util.SharedPreferencesUtil

private const val TAG = "MainActivity_싸피"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var checkPermission: CheckPermission
    private var permissionGranted = false
    lateinit var bottomNav: BottomNavigationView

    val PERMISSION_REQUEST_CODE = 8

    init {
        instance = this
    }

    companion object {
        var shakeDetector = ShakeDetector()
        var fromMMSReceiver: Bitmap? = null
        const val channel_id = "popcon_user"

        private var instance: MainActivity? = null
        fun getInstance(): MainActivity? {
            return instance
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNav = binding.bottomNav
        Log.d(TAG, "keyhash : ${Utility.getKeyHash(this)}")

        setNavBar()
        checkPermissions()
        callMMSReceiver()
        chkNewMMSImg()

        //자동로그인
        if (SharedPreferencesUtil(this).getUser().email != "") {
            Log.d(TAG, "onCreate: 로그인됨")
            sendUserData()
            changeFragment(HomeFragment())
            makeGalleryDialogFragment(applicationContext, contentResolver)
        } else {
            Log.d(TAG, "onCreate: 로그인 필요")
            changeFragment(LoginFragment())
        }
    }

    // 앱 실행 시 gallery에서 이미지 불러오기
    @RequiresApi(Build.VERSION_CODES.Q)
    fun makeGalleryDialogFragment(
        appliContext: Context,
        cResolver: ContentResolver
    ) {
        val addGalleryGifticon = AddGalleryGifticon(
            this, appliContext, cResolver
        )

        getInstance()!!.supportFragmentManager.beginTransaction()
            .add(addGalleryGifticon, "galleryDialog")
            .commitAllowingStateLoss()
    }

    // data send to watch
    private fun sendUserData() {
        val payload: ByteArray =
            (SharedPreferencesUtil(this@MainActivity).getUser().email!! + " " + SharedPreferencesUtil(
                this@MainActivity
            ).getUser().social!! + " " + ApplicationClass.sharedPreferencesUtil.accessToken).toByteArray()

        val sendMessageTask =
            Wearable.getMessageClient(this)
                .sendMessage("nodeId", "/user", payload)

        sendMessageTask.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("send1", "Message sent successfully")
            } else {
                Log.d("send1", "Message failed.")
            }
        }
    }

    // MMS BroadcastReceiver 호출위한 JobScheduler
    private fun callMMSReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val job = JobInfo.Builder(
            0,
            ComponentName(this, MMSJobService::class.java)
        )
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build()

        jobScheduler.schedule(job)
    }

    private fun chkNewMMSImg() {
        if (fromMMSReceiver != null) {
            supportFragmentManager.beginTransaction()
                .add(MMSDialog(this), "mmsDialog")
                .commitAllowingStateLoss()
        }
    }

    fun updateStatusBarColor(color: String?) { // Color must be in hexadecimal fromat
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor(color)
    }

    //navigation bar 설정
    private fun setNavBar() {
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
                R.id.mapFragment -> {
                    changeFragment(MapFragment())
                    true
                }
                R.id.settingsFragment -> {
                    changeFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        //재선택 방지
        binding.bottomNav.setOnItemReselectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mapFragment -> {}
                R.id.addFragment -> {}
                R.id.settingsFragment -> {}
                R.id.homeFragment -> {}
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

    override fun onResume() {
        super.onResume()
        chkNewMMSImg()
    }

    private val runtimePermissions = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECEIVE_MMS,
        Manifest.permission.READ_SMS
    )

    // 위치, 갤러리, 전화 권한
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
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                    && grantResults[5] == PackageManager.PERMISSION_GRANTED
                    && grantResults[6] == PackageManager.PERMISSION_GRANTED
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

    // 알림 관련 메시지 전송
    suspend fun sendMessageTo(token: String, title: String, body: String) {
        FCMRepository(FCMRemoteDataSource(RetrofitUtil.fcmService)).sendMessageTo(
            token,
            title,
            body
        )
    }

    override fun onRestart() {
        super.onRestart()
        //checkPermissions()
    }
}