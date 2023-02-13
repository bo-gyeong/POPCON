package com.ssafy.popcon.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ssafy.popcon.MainActivity
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ActivityDonateBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.ui.common.DragListener
import com.ssafy.popcon.util.MyLocationManager
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.WearViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactoryWear

private const val TAG = "MapFragment"
class DonateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonateBinding
    private val ACCESS_FINE_LOCATION = 1000     // Request Code
    lateinit var lm: LocationManager
    var mainActivity = MainActivity()
    private val viewModel: WearViewModel by viewModels { ViewModelFactoryWear(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //SharedPreferencesUtil(this).addUser(User("abc@naver.com", "카카오"))


        binding = ActivityDonateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lm = MyLocationManager.getLocationManager(this)

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

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    lateinit var donateNumber: String

    //기프티콘 뷰페이저
    private fun setGifticonBanner() {
        val user = SharedPreferencesUtil(this).getUser()
        var gifticonAdapter = MapGifticonAdpater(viewModel, user, lm)

        gifticonAdapter.setOnLongClickListener(object : MapGifticonAdpater.OnLongClickListener {
            override fun onLongClick(v: View, gifticon: Gifticon) {
                donateNumber = gifticon.barcodeNum
                gifticonAdapter.setOnDragListener(
                    DragListener(
                        gifticon.barcodeNum,
                        viewModel,
                        user,
                        lm
                    )
                )
            }
        })

        viewModel.getGifticonByUser(SharedPreferencesUtil(this).getUser())

        with(binding.viewpagerMapGiftcon) {
            adapter = gifticonAdapter.apply {
                viewModel.gifticons.observe(this@DonateActivity) {
                    submitList(it)
                }
            }

            val pageWidth = resources.getDimension(R.dimen.viewpager_item_widwth)
            val pageMargin = resources.getDimension(R.dimen.viewpager_item_margin)
            val screenWidth = resources.displayMetrics.widthPixels
            val offset = screenWidth - pageWidth - pageMargin

            offscreenPageLimit = 3
            setPageTransformer { page, position ->
                page.translationX = position * -offset
            }
        }
    }
}

