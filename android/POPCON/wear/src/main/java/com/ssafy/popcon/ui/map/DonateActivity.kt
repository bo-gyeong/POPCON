package com.ssafy.popcon.ui.map

import android.Manifest
import android.content.ClipData
import android.content.ClipDescription
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ssafy.popcon.MainActivity
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ActivityDonateBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.ui.common.WearDragListener
import com.ssafy.popcon.ui.common.DragShadowBuilder
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.WearViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactoryWear

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
                if (ActivityCompat.checkSelfPermission(
                        this@DonateActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@DonateActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.

                        DonateLocation.x = location?.longitude.toString()
                        DonateLocation.y = location?.latitude.toString()
                    }

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
}

