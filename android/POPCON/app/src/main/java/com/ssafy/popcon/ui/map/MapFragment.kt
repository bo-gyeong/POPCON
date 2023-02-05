package com.ssafy.popcon.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


private const val TAG = "MapFragment"

class MapFragment : Fragment(), CalloutBalloonAdapter {
    private lateinit var binding: FragmentMapBinding
    private val ACCESS_FINE_LOCATION = 1000     // Request Code
    private lateinit var ballBinding: ItemBalloonBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: MapViewModel by activityViewModels { ViewModelFactory(requireContext()) }

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

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
    }

    // 위치추적 중지
    private fun stopTracking() {
        binding.mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOff
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
            binding.mapView.removeAllPOIItems()
            binding.mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(it))

            for (store in it) {
                val marker = MapPOIItem()
                val position = MapPoint.mapPointWithGeoCoord(
                    store.ypos.toDouble(),
                    store.xpos.toDouble()
                )
                marker.itemName = store.placeName
                marker.mapPoint = position

                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageBitmap = drawableFromUrl(store.brandInfo.brandImg!!)
                marker.isCustomImageAutoscale = false // 커스텀 마커 이미지 크기 자동 조정

                markers.add(marker)
                binding.mapView.addPOIItem(marker)
            }
        }
    }

    fun resizeBitmapImage(source: Bitmap, maxResolution: Int): Bitmap? {
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
    fun setGifticonBanner() {
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

    inner class CustomBalloonAdapter(private val stores: List<Store>) :
        CalloutBalloonAdapter {
        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            // 마커 클릭 시 나오는 말풍선
            val name = poiItem?.itemName
            for (store in stores) {
                if (store.placeName == name) {
                    ballBinding.store = store
                    break
                }
            }

            return ballBinding.root
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
            return ballBinding.root
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
    // 1. 비트맵 사이즈 비율대로 줄이고 내부 저장소에 저장하는 함수

    // 4. imgUrl을 Drawable로 바꿔주는 함수
    private fun drawableFromUrl(url: String): /*Drawable*/Bitmap {
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
}
/*
class MapFragment : Fragment(), CalloutBalloonAdapter {
    private lateinit var binding: FragmentMapBinding
    private lateinit var ballBinding: ItemBalloonBinding
    private lateinit var locationManager: LocationManager
    private var getLongitude: Double = 0.0
    private var getLatitude: Double = 0.0
    private lateinit var internalStorage: String
    private val viewModel: MapViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapBinding.inflate(inflater, container, false)
        ballBinding = ItemBalloonBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mapView = binding.mapView

        setGifticonBanner()
        setStore()
        setSensor()

        // 로고 이미지를 저장할 위치 - 내부 저장소
        internalStorage = requireContext().filesDir.toString() + "/brandLogo"

        // 사용자 현재 위치 가져오기, GPS 우선
        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        getUserLocation()



        // 위치 업데이트 버튼 클릭시 화면 가운데를 현재 위치 변경
        binding.btnUpdatePosition.setOnClickListener {
            moveMapUserToPosition(mapView)
        }
        // imgUrl to Drawable 함수 사용 할 때 필요함 삭제 ㄴㄴ
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        // 1. 내부저장소에 로고만 저장할 폴더 없으면 만들기
        val path =
            File("$internalStorage") // internalStorage = requireContext().filesDir.toString() + "/brandLogo"
        if (!path.exists()) {
            path.mkdirs()
        }
    }

    // 현 위치 마커 추가
    private fun makeCurrPoint() {
        getUserLocation()

        var currentMarker = MapPOIItem()
        currentMarker.apply {
            itemName = "현위치"
            mapPoint = MapPoint.mapPointWithGeoCoord(getLatitude, getLongitude)
            currentMarker.markerType = MapPOIItem.MarkerType.CustomImage // 기본으로 제공되는 파란색 핀
            customImageResourceId = R.drawable.popcon_point
        }
        binding.mapView.addPOIItem(currentMarker)
    }

    inner class CustomBalloonAdapter(private val stores: List<Store>) :
        CalloutBalloonAdapter {
        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            // 마커 클릭 시 나오는 말풍선
            val name = poiItem?.itemName
            for (store in stores) {
                if (store.placeName == name) {
                    ballBinding.store = store
                    break
                }
            }

            return ballBinding.root
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
            return ballBinding.root
        }
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
            binding.mapView.removeAllPOIItems()

            Log.d(TAG, "setStore: $it")
            binding.mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(it))

            for (store in it) {
                val marker = MapPOIItem()
                val position = MapPoint.mapPointWithGeoCoord(
                    store.ypos.toDouble(),
                    store.xpos.toDouble()
                )

                marker.itemName = store.placeName
                marker.mapPoint = position
                //marker.markerType = MapPOIItem.MarkerType.BluePin
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                //marker.customImageResourceId = R.drawable.drawableFromUrl("https://contents.lotteon.com/search/brand/P2/38/6/P2386_320_320.jpg/dims/optimize/dims/resize/400x400")
                //marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

                markers.add(marker)
                binding.mapView.addPOIItem(marker)
            }

            makeCurrPoint()
        }
    }

    //기프티콘 뷰페이저
    fun setGifticonBanner() {
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

    // 5. 현재 위치로 중심점 변경하는 함수
    private fun moveMapUserToPosition(mapView: MapView) {
        getUserLocation()

        mapView.setMapCenterPointAndZoomLevel(
            MapPoint.mapPointWithGeoCoord(
                getLatitude,
                getLongitude
            ), 3, true
        )
    }

    // 6. 사용자 위치 받아오는 함수
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

    // 1. 비트맵 사이즈 비율대로 줄이고 내부 저장소에 저장하는 함수
    fun resizeBitmap(fileName: String, brand: String) {
        val tempFile = File(internalStorage, fileName)
        try {
            tempFile.createNewFile()  // 빈 파일을 생성
            val out = FileOutputStream(tempFile) // 파일을 쓸 수 있는 스트림 준비
            var logoUrl = logoImageUrl(brand) // 브랜드에 따라 로고 url 받아오는 함수
            var bitmap = drawableFromUrl(logoUrl).toBitmap() // 로고url에 있는걸 비트맵으로 바꾸는 함수

            // 이미지 크기에 따라 압축률 정하기
            var quality = 0.25
            if (bitmap.width > 2048 && bitmap.height > 2048) {
                quality = 0.7
            } else if (bitmap.width > 1024 && bitmap.height > 1024) {
            }
            quality = 0.4
            // 비트맵 압축률만큼 압축하기
            bitmap = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * quality).toInt(),
                (bitmap.height * quality).toInt(),
                true
            )
            // 비트맵 저장
            saveBitmap(bitmap, tempFile.toString())
            // 리소스 해제 및 닫기
            out.close()
            bitmap.recycle() // 비트맵은 리소스 많이 먹어서 해제 필수
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
        }
    }

    // 2. 비트맵 저장하는 함수
    fun saveBitmap(bitmap: Bitmap, filePath: String): String {
        var out = FileOutputStream(filePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        return filePath
    }

    // 3. 브랜드에 따라 (서버에 올린?) 로고 이미지 주소 리턴하는 함수
    fun logoImageUrl(brand: String): String {
        return when (brand) {
            "스타벅스" -> "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
            "이디야" -> ""
            else -> ""
        }
    }

    // 4. imgUrl을 Drawable로 바꿔주는 함수
    private fun drawableFromUrl(url: String): Drawable {
        val x: Bitmap
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val input: InputStream = connection.getInputStream()
        x = BitmapFactory.decodeStream(input)
        return BitmapDrawable(Resources.getSystem(), x)
    }

    //배너 클릭하면, 기프티콘, 매장위치 받아와야함.
    */
/*private fun topBannerClickListener() {
        // 나중에 서버에서 받아올 가게 정보 = storeList
        viewModel.mapBrandLogo.observe(viewLifecycleOwner, Observer {
            // 2. 해당 브랜드들 미리 내부 저장소에 다운 시키기 -> 이미 저장되어 있으면 안하고, 내부 저장소에 저장함. Device File Explorer ㄱㄱ
            for (store in it) {
                val files = path.listFiles()
                var alreadyStore = false
                for (file in files) {
                    if (store.brandName == file.name) {
                        alreadyStore = true
                        break
                    }
                }
                // 2-1. 브랜드 로고 없다면 추가
                if (!alreadyStore) {
                    // 0. storage에 파일 인스턴스를 생성합니다.
                    fileName = "${store.brandName}.jpg" // 파일 이름은 브랜드 한글 이름
                    // 1. resizeBitmap
                    resizeBitmap(fileName, store.brandName)
                }
                // 3. 없다면, 인터넷에 있는 사진을 다운
                // 4. 다운 받고 사이즈를 줄여서 다시 저장
            }

            // 지도에서 마커 추가하기
            for (store in it) {
                var tempMarker = MapPOIItem()
                tempMarker.apply {
                    itemName = store.itemName
                    mapPoint = MapPoint.mapPointWithGeoCoord(
                        store.xPos!!.toDouble(),
                        store.yPos!!.toDouble()
                    )
                    markerType = MapPOIItem.MarkerType.CustomImage
                    var filePath = File(internalStorage, "/$fileName") // internalStorage = requireContext().filesDir.toString() + "/brandLogo"
                    customImageBitmap = BitmapFactory.decodeFile(filePath.toString())
                    isCustomImageAutoscale = false // 커스텀 마커 이미지 크기 자동 조정
                }
                mapView.addPOIItem(tempMarker)
            }
        })
    }*//*


    override fun getCalloutBalloon(p0: MapPOIItem?): View {
        TODO("Not yet implemented")
    }

    override fun getPressedCalloutBalloon(p0: MapPOIItem?): View {
        TODO("Not yet implemented")
    }
}
*/
