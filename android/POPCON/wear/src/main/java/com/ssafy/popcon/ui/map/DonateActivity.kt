package com.ssafy.popcon.ui.map

import android.content.ClipData
import android.content.ClipDescription
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.popcon.MainActivity
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ActivityDonateBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.ui.common.WearDragListener
import com.ssafy.popcon.ui.common.DragShadowBuilder
import com.ssafy.popcon.util.MyLocationManager
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.WearViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactoryWear

private const val TAG = "MapFragment"

class DonateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonateBinding
    private val ACCESS_FINE_LOCATION = 1000     // Request Code
    var mainActivity = MainActivity()
    private val viewModel: WearViewModel by viewModels { ViewModelFactoryWear(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        checkLocationService()

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

    lateinit var lm: LocationManager
    private fun checkLocationService(): Boolean {
        lm = MyLocationManager.getLocationManager(this)
        Log.d(TAG, "checkLocationService: $lm")
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    lateinit var donateNumber: String

    //기프티콘 뷰페이저
    private fun setGifticonBanner() {
        val user = SharedPreferencesUtil(this).getUser()
        var gifticonAdapter = MapGifticonAdpater(
            binding.tvDonate,
            viewModel,
            user,
            lm
        )

        gifticonAdapter.setOnLongClickListener(object : MapGifticonAdpater.OnLongClickListener {
            override fun onLongClick(v: View, gifticon: Gifticon) {
                donateNumber = gifticon.barcodeNum

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
                        user,
                        lm
                    )
                )
                binding.tvDonate.setOnDragListener(
                    WearDragListener(
                        binding.tvDonate,
                        gifticon.barcodeNum,
                        viewModel,
                        user,
                        lm
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
}

