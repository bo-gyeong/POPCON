package com.ssafy.popcon.ui.map

import android.Manifest
import android.content.ClipData
import android.content.ClipDescription
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
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ssafy.popcon.R
import com.ssafy.popcon.config.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.popcon.databinding.FragmentMapBinding
import com.ssafy.popcon.databinding.ItemBalloonBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.ui.common.DragListener
import com.ssafy.popcon.ui.common.DragShadowBuilder
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.util.MyLocationManager
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.MapViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.CurrentLocationEventListener
import net.daum.mf.map.api.MapView.MapViewEventListener
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


private const val TAG = "MapFragment"

class MapFragment : Fragment(), CalloutBalloonAdapter, MapViewEventListener,
    MapView.POIItemEventListener, CurrentLocationEventListener {
    private lateinit var binding: FragmentMapBinding
    private val ACCESS_FINE_LOCATION = 1000     // Request Code
    lateinit var lm: LocationManager
    private lateinit var ballBinding: ItemBalloonBinding
    private var mode = 1
    lateinit var mainActivity: MainActivity
    private val viewModel: MapViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    var storeMap = HashMap<String, String>()
    var markers = mutableListOf<MapPOIItem>()
    var presentMarkers = mutableListOf<MapPOIItem>()

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
        lm = MyLocationManager.getLocationManager(requireContext())
        binding.mapView.setMapViewEventListener(this)
        binding.mapView.setCalloutBalloonAdapter(this)
        binding.mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))
        binding.mapView.setPOIItemEventListener(this)
        binding.mapView.setCurrentLocationEventListener(this)

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

        // 위치 업데이트 버튼 클릭시 화면 가운데를 현재 위치 변경
        binding.btnUpdatePosition.setOnClickListener {
            mode = 1
            moveMapUserToPosition(binding.mapView)
            startTracking()
        }

        binding.btnFind.setOnClickListener {
            //moveMapUserToPosition(binding.mapView)
            mode = 0
            startTracking()
            findPresent()
        }
    }

    private fun findPresent() {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getUserLocation()
        //y : 위도 latitude 경 127도, 위 37도
        viewModel.getPresents(
            FindPresentRequest(
                getLongitude.toString(),
                getLatitude.toString()
            )
        )
        viewModel.presents.observe(viewLifecycleOwner) {
            binding.mapView.removeAllPOIItems()

            for (present in it) {
                val marker = MapPOIItem()
                val position = MapPoint.mapPointWithGeoCoord(
                    present.y.toDouble(),
                    present.x.toDouble()
                )
                marker.itemName = present.barcodeNum
                marker.mapPoint = position

                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.far
                marker.isCustomImageAutoscale = false // 커스텀 마커 이미지 크기 자동 조정

                presentMarkers.add(marker)
                binding.mapView.addPOIItem(marker)
            }
        }

        viewModel.presentsNear.observe(viewLifecycleOwner) {
            for (present in it) {
                val marker = MapPOIItem()
                val position = MapPoint.mapPointWithGeoCoord(
                    present.y.toDouble(),
                    present.x.toDouble()
                )
                marker.itemName = present.barcodeNum
                marker.mapPoint = position

                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.near
                marker.isCustomImageAutoscale = false // 커스텀 마커 이미지 크기 자동 조정

                presentMarkers.add(marker)
                binding.mapView.addPOIItem(marker)
            }
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

        binding.mapView.setCustomCurrentLocationMarkerImage(
            R.drawable.popcon_point,
            MapPOIItem.ImageOffset(10, 10)
        )
        //binding.mapView.setShowCurrentLocationMarker(false)
    }

    private fun setStore() {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getUserLocation()

        val request = StoreRequest(
            sharedPreferencesUtil.getUser().email!!,
            sharedPreferencesUtil.getUser().social,
            /*getLongitude.toString(),
            getLatitude.toString()*/
            "128.4166327872964",
            "36.10747083607294"
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

    lateinit var donateNumber: String

    //기프티콘 뷰페이저
    private fun setGifticonBanner() {
        val user = SharedPreferencesUtil(requireContext()).getUser()
        val targetView = binding.ivPresent
        var gifticonAdapter = MapGifticonAdpater(targetView, viewModel, user, lm)
        gifticonAdapter.setOnLongClickListener(object : MapGifticonAdpater.OnLongClickListener {
            override fun onLongClick(v: View, gifticon: Gifticon) {
                Log.d(TAG, "onLongClick: $gifticon")
                val item = ClipData.Item(v.tag as? CharSequence)
                val dragData = ClipData(
                    v.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val shadow = DragShadowBuilder.fromResource(requireContext(), R.drawable.present)
                val location = binding.mapView.mapCenterPoint.mapPointGeoCoord
                donateNumber = gifticon.barcodeNum

                val req = DonateRequest(
                    gifticon.barcodeNum,
                    location.longitude.toString(),
                    location.latitude.toString()
                )
                gifticonAdapter.setOnDragListener(
                    DragListener(
                        targetView,
                        req,
                        viewModel,
                        user,
                        lm
                    )
                )
                binding.ivPresent.setOnDragListener(
                    DragListener(
                        targetView,
                        req,
                        viewModel,
                        user,
                        lm
                    )
                )

                v.startDrag(dragData, shadow, v, 0)
            }
        })

        viewModel.getGifticonByUser(SharedPreferencesUtil(requireContext()).getUser())

        with(binding.viewpagerMapGiftcon) {
            adapter = gifticonAdapter.apply {
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
            if (storeMap.containsKey(poiItem?.itemName)) {//매장 핀
                name.text = poiItem?.itemName
                phone.text = storeMap[poiItem?.itemName]
            } else {//기부 핀
                phone.isVisible = false
                if (poiItem?.customImageResourceId == R.drawable.near) {
                    name.text = "줍기"
                } else {
                    val view: CardView = mCalloutBalloon.findViewById(R.id.ballView)
                    view.setCardBackgroundColor(Color.parseColor("#FF9797"))
                    name.text = "더 가까이 이동하세요"
                }
            }

            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
            if (mode == 1) {
                stopTracking()
            }
            return mCalloutBalloon
        }
    }


    private fun resizeBitmapFromUrl(url: String): Bitmap {
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

    //지도 리스너
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
        if (mode == 1) {
            stopTracking()
        }
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        if (mode == 1) {
            val user = SharedPreferencesUtil(requireContext()).getUser()
            val location = binding.mapView.mapCenterPoint.mapPointGeoCoord
            if (viewModel.brandName == "전체") {
                viewModel.getStoreInfo(
                    StoreRequest(
                        user.email,
                        user.social,
                        location.longitude.toString(),
                        location.latitude.toString()
                    )
                )
            } else {
                viewModel.getStoreByBrand(
                    StoreByBrandRequest(
                        viewModel.brandName,
                        user.email!!,
                        user.social,
                        location.longitude.toString(), location.latitude.toString()
                    )
                )
            }
        }
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    //마커 리스너
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
        if (mode == 1) {
            var intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + ballBinding.tvPhone.text)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        } else {
            if (p1?.customImageResourceId == R.drawable.near) {
                getUserLocation()
                viewModel.getPresent(
                    GetPresentRequest(
                        p1!!.itemName,
                        "",
                        getLongitude.toString(),
                        getLatitude.toString()
                    ), SharedPreferencesUtil(requireContext()).getUser()
                )
            }
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

    //트래킹 리스너
    override fun onCurrentLocationUpdate(p0: MapView?, p1: MapPoint?, p2: Float) {

        Log.d(
            TAG, "onCurrentLocationUpdate: ${p1!!.mapPointGeoCoord.longitude.toString()} + ${
                p1!!.mapPointGeoCoord.latitude
            }"
        )
        viewModel.getPresents(
            FindPresentRequest(
                p1!!.mapPointGeoCoord.longitude.toString(),
                p1!!.mapPointGeoCoord.latitude.toString()
            )
        )
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {

    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {

    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {

    }
}