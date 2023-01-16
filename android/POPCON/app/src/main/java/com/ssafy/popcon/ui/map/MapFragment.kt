package com.ssafy.popcon.ui.map

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ssafy.popcon.databinding.FragmentMapBinding
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import com.ssafy.popcon.util.ShakeDetector
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

private const val TAG = "MapFragment 지원"

class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var locationManager: LocationManager
    private var getLongitude: Double = 0.0
    private var getLatitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSensor()

        // 맵 띄우기
        val mapView = MapView(requireContext())
        val mapViewContainer = binding.mapView as ViewGroup
        mapViewContainer.addView(mapView)

        // 사용자 현재 위치 가져오기, GPS 우선
        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        getUserLocation()

        // 현재 위치로 중심점 변경
        moveMapUserToPosition(mapView)

        // 트래킹모드
//        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
//        mapView.setShowCurrentLocationMarker(true) // 트래킹모드일 경우, 현재 위치 파란색 동그라미로 표시

        // 위치 업데이트 버튼 클릭시 화면 가운데를 현재 위치 변경
        binding.btnUpdatePosition.setOnClickListener {
            moveMapUserToPosition(mapView)
        }

        // 마커 추가
        var currentMarker = MapPOIItem()
        currentMarker.apply {
            itemName = "현위치"
            mapPoint = MapPoint.mapPointWithGeoCoord(getLatitude, getLongitude)
            currentMarker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공되는 파란색 핀
        }
        mapView.addPOIItem(currentMarker)


        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return // 권한 요청 받으라는데 안 받을거임
        }


    }

    private fun moveMapUserToPosition(mapView: MapView) {
        // 현재 위치로 중심점 변경
        mapView.setMapCenterPointAndZoomLevel(
            MapPoint.mapPointWithGeoCoord(
                getLatitude,
                getLongitude
            ), 3, true
        )
    }

    // 위에 *몇 초 간격과 몇미터를 이동 했을 때 호출 되는 부분에 대한 필요한 정보, 주기적으로 위치 업데이트하는 경우 사용
    val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            getLatitude = location.latitude
            getLongitude = location.longitude
            Log.d(TAG, "onLocationChanged: 변경됨")
        }

        // 아래 3개 함수는 형식상 필수 부분
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            super.onStatusChanged(provider, status, extras)
        }

        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
        }
    }


    // 사용자 위치 받아오는 함수
    private fun getUserLocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        } else {
            when {
                isGPSEnable -> {
                    val location =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    getLongitude = location?.longitude!!
                    getLatitude = location?.latitude!!
                    Log.d(TAG, "getUserLocation: GPS --> lon : $getLongitude, lat : $getLatitude")
                }
                isNetworkEnable -> {
                    val location =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    getLongitude = location?.longitude!!
                    getLatitude = location?.latitude!!
                    Log.d(
                        TAG,
                        "getUserLocation: Network  --> lon : $getLongitude, lat : $getLatitude"
                    )
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        // 프래그먼트가 hide 상태인 경우 = 사용자가 볼 수 없는 경우 = 실시간 사용자 위치 트래킹 종료
        locationManager.removeUpdates(gpsLocationListener)
    }

    //화면 켜지면 센서 설정
    private fun setSensor() {
        shakeDetector = ShakeDetector()
        shakeDetector.setOnShakeListener(object : ShakeDetector.OnShakeListener {
            override fun onShake(count: Int) {
                if (!isShow) {
                    activity?.let {
                        GifticonDialogFragment().show(it.supportFragmentManager, "popup")
                    }
                }
            }
        })

        MainActivity().setShakeSensor(requireContext(), shakeDetector)
    }
}

