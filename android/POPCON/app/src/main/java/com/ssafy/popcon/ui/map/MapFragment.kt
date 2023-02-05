package com.ssafy.popcon.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ssafy.popcon.R
import com.ssafy.popcon.config.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.popcon.databinding.FragmentMapBinding
import com.ssafy.popcon.databinding.ItemBalloonBinding
import com.ssafy.popcon.dto.Store
import com.ssafy.popcon.dto.StoreRequest
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.common.MainActivity.Companion.shakeDetector
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import com.ssafy.popcon.util.ShakeDetector
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.MapViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.MapViewEventListener
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


private const val TAG = "MapFragment"

class MapFragment : Fragment(), CalloutBalloonAdapter, MapViewEventListener, MapView.POIItemEventListener {
    private lateinit var binding: FragmentMapBinding
    private val ACCESS_FINE_LOCATION = 1000     // Request Code
    private lateinit var ballBinding: ItemBalloonBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: MapViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    var storeMap = HashMap<String, String>()

    private lateinit var locationManager: LocationManager
    private var getLongitude: Double = 0.0
    private var getLatitude: Double = 0.0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapBinding.inflate(inflater, container, false)
        ballBinding = ItemBalloonBinding.inflate(inflater, container, false)

        moveMapUserToPosition(binding.mapView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        binding.mapView.setMapViewEventListener(this)
        binding.mapView.setCalloutBalloonAdapter(this)
        binding.mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))
        binding.mapView.setPOIItemEventListener(this)

        if (checkLocationService()) {
            // GPS가 켜져있을 경우
            mainActivity.checkPermissions()
            startTracking()
        } else {
            // GPS가 꺼져있을 경우
            Toast.makeText(requireContext(), "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
        }

        setGifticonBanner()
        setStore()
        setSensor()

        // 위치 업데이트 버튼 클릭시 화면 가운데를 현재 위치 변경
        binding.btnUpdatePosition.setOnClickListener {
            moveMapUserToPosition(binding.mapView)
            startTracking()
        }
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
                Toast.makeText(requireContext(), "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                startTracking()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(requireContext(), "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
                mainActivity.checkPermissions()
            }
        }
    }

    private fun moveMapUserToPosition(mapView: MapView) {
        getUserLocation()

        mapView.setMapCenterPointAndZoomLevel(
            MapPoint.mapPointWithGeoCoord(
                getLatitude,
                getLongitude
            ), 3, true
        )
    }

    private fun getUserLocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
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
            var location =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null) {
                location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            getLongitude = location?.longitude!!
            getLatitude = location?.latitude!!
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치추적 시작
    private fun startTracking() {
        binding.mapView.setCustomCurrentLocationMarkerTrackingImage(
            R.drawable.popcon_point,
            MapPOIItem.ImageOffset(10, 10)
        )
        binding.mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading

        binding.mapView.setShowCurrentLocationMarker(true)
    }

    // 위치추적 중지
    private fun stopTracking() {
        binding.mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOff

        binding.mapView.setShowCurrentLocationMarker(false)
    }

    var markers = mutableListOf<MapPOIItem>()
    private fun setStore() {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getUserLocation()

        val request = StoreRequest(
            sharedPreferencesUtil.getUser().email!!,
            sharedPreferencesUtil.getUser().social,
            getLongitude.toString(),
            getLatitude.toString()
        )

        //y : 위도 latitude 경 127도, 위 37도
        viewModel.getStoreInfo(request)
        viewModel.store.observe(viewLifecycleOwner) {
            storeMap.clear()
            binding.mapView.removeAllPOIItems()

            for (store in it) {
                storeMap.put(store.placeName, store.phone)
                val marker = MapPOIItem()
                val position = MapPoint.mapPointWithGeoCoord(
                    store.ypos.toDouble(),
                    store.xpos.toDouble()
                )
                marker.itemName = store.placeName
                marker.mapPoint = position

                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageBitmap = resizeBitmapFromUrl(store.brandInfo.brandImg!!)
                marker.isCustomImageAutoscale = false // 커스텀 마커 이미지 크기 자동 조정

                markers.add(marker)
                binding.mapView.addPOIItem(marker)
            }
        }
    }

    private fun resizeBitmapImage(source: Bitmap, maxResolution: Int): Bitmap? {
        val width = source.width
        val height = source.height
        var newWidth = width
        var newHeight = height
        var rate = 0.0f
        if (width > height) {
            if (maxResolution < width) {
                rate = maxResolution / width.toFloat()
                newHeight = (height * rate).toInt()
                newWidth = maxResolution
            }
        } else {
            if (maxResolution < height) {
                rate = maxResolution / height.toFloat()
                newWidth = (width * rate).toInt()
                newHeight = maxResolution
            }
        }
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }

    //기프티콘 뷰페이저
    private fun setGifticonBanner() {
        viewModel.getGifticonByUser(SharedPreferencesUtil(requireContext()).getUser())

        with(binding.viewpagerMapGiftcon) {
            adapter = MapGifticonAdpater().apply {
                viewModel.mapGifticon.observe(viewLifecycleOwner) {
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

    override fun getCalloutBalloon(p0: MapPOIItem?): View {
        TODO("Not yet implemented")
    }

    override fun getPressedCalloutBalloon(p0: MapPOIItem?): View {
        TODO("Not yet implemented")
    }

    inner class CustomBalloonAdapter(inflater: LayoutInflater) : CalloutBalloonAdapter {
        private val mCalloutBalloon: View = inflater.inflate(R.layout.item_balloon, null)
        val name: TextView = mCalloutBalloon.findViewById(R.id.tv_brandName)
        val phone: TextView = mCalloutBalloon.findViewById(R.id.tv_phone)

        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            // 마커 클릭 시 나오는 말풍선
            name.text = poiItem?.itemName
            phone.text = storeMap[poiItem?.itemName]

            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
            stopTracking()
            return mCalloutBalloon
        }
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

    private fun resizeBitmapFromUrl(url: String): /*Drawable*/Bitmap {
        val x: Bitmap
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val input: InputStream = connection.inputStream
        x = BitmapFactory.decodeStream(input)

        val output = Bitmap.createBitmap(x.width, x.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, x.width, x.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(x.width / 2f, x.height / 2f, x.width / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(x, rect, rect, paint)

        return resizeBitmapImage(output, 60)!!
    }

    override fun onMapViewInitialized(p0: MapView?) {
        Log.d(TAG, "onMapViewInitialized: ")
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        stopTracking()
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {


    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

        var intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + ballBinding.tvPhone.text)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }
}