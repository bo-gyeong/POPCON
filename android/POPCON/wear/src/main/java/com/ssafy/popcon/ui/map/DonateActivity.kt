package com.ssafy.popcon.ui.map

import android.Manifest
import android.content.ClipData
import android.content.ClipDescription
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.ssafy.popcon.MainActivity
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ActivityDonateBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.ui.common.DragShadowBuilder
import com.ssafy.popcon.ui.common.WearDragListener
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.ViewModelFactoryWear
import com.ssafy.popcon.viewmodel.WearViewModel


private const val TAG = "MapFragment"

object DonateLocation {
    var x: String = ""
    var y: String = ""
}

class DonateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonateBinding
    private val ACCESS_FINE_LOCATION = 1000     // Request Code
    var mainActivity = MainActivity()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: WearViewModel by viewModels { ViewModelFactoryWear(this) }

    private val requestCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            //Timber.d("Update Latitude : ${p0.lastLocation.latitude} \nUpdate Longitude : ${p0.lastLocation.longitude}")
            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityDonateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setGifticonBanner()
    }

    // 권한 요청 후 행동
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
                mainActivity.checkPermissions()
            }
        }
    }

    private var isContinue = false
    private lateinit var locationRequest: LocationRequest

    private fun getLocation() {
        locationRequest = LocationRequest.create()
        //앱에 위치 권한이 있는 없는 경우
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1000
            ) //위치권한 요청
        } else {    //이미 앱에 위치 권한이 있는 경우
            if (isContinue) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                ) //Request객체를 전달하고 그 결과를 콜백함수에서 전달 받는다
            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener(this) { location ->
                        if (location != null) {
                            DonateLocation.y = location.latitude.toString()
                            DonateLocation.x = location.longitude.toString()
                        } else {
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                null
                            )
                        }
                    }
            }
        }
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(@NonNull locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            DonateLocation.y = locationResult.lastLocation!!.latitude.toString()
            DonateLocation.x = locationResult.lastLocation!!.longitude.toString()
            fusedLocationClient.removeLocationUpdates(this) //결과 전달 되면 리스너 삭제
        }
    }
    lateinit var donateNumber: String

    //기프티콘 뷰페이저
    private fun setGifticonBanner() {
        val user = SharedPreferencesUtil(this).getUser()
        var gifticonAdapter = MapGifticonAdpater(
            binding.tvDonate,
            viewModel,
            user
        )

        gifticonAdapter.setOnLongClickListener(object : MapGifticonAdpater.OnLongClickListener {
            override fun onLongClick(v: View, gifticon: Gifticon) {
                donateNumber = gifticon.barcodeNum

                getLocation()

                val item = ClipData.Item(v.tag as? CharSequence)
                val dragData = ClipData(
                    v.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val shadow = DragShadowBuilder.fromResource(this@DonateActivity, R.drawable.present)
                gifticonAdapter.setOnDragListener(
                    WearDragListener(
                        binding.tvDonate,
                        gifticon.barcodeNum,
                        viewModel,
                        user
                    )
                )
                binding.tvDonate.setOnDragListener(
                    WearDragListener(
                        binding.tvDonate,
                        gifticon.barcodeNum,
                        viewModel,
                        user
                    )
                )
                v.startDrag(dragData, shadow, v, 0)
            }
        })

        viewModel.getGifticonByUser(SharedPreferencesUtil(this).getUser())

        with(binding.viewpagerMapGiftcon) {
            adapter = gifticonAdapter.apply {
                viewModel.gifticons.observe(this@DonateActivity) {
                    submitList(it)
                }
            }
        }
    }

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
}

