package com.ssafy.popcon.ui.map

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.R.attr.*
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.ssafy.popcon.databinding.FragmentMapBinding
import com.ssafy.popcon.dto.MapBrandLogo
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.common.MainActivity.Companion.shakeDetector
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import com.ssafy.popcon.util.ShakeDetector
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "MapFragment 지원"

class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var locationManager: LocationManager
    private var getLongitude: Double = 0.0
    private var getLatitude: Double = 0.0
    private lateinit var internalStorage: String
    private lateinit var fileName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // imgUrl to Drawable할때 필요해요!
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setSensor()

        internalStorage = requireContext().filesDir.toString() + "/brandLogo"


        // 맵 띄우기
        val mapView = MapView(requireContext())
        val mapViewContainer = binding.mapView as ViewGroup
        mapViewContainer.addView(mapView)

        // 사용자 현재 위치 가져오기, GPS 우선
        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        getUserLocation()

        // 현재 위치로 중심점 변경
        moveMapUserToPosition(mapView)

        // 위치 업데이트 버튼 클릭시 화면 가운데를 현재 위치 변경
        binding.btnUpdatePosition.setOnClickListener {
            moveMapUserToPosition(mapView)
        }


        // 현 위치 마커 추가
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

        // 나중에 서버에서 받아올 가게 정보
        var storeResult = ArrayList<MapBrandLogo>()
        storeResult.add(
            MapBrandLogo(
                "스타벅스",
                "구미 인동점",
                36.1079891,
                128.418535,
                "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
            )
        )
        storeResult.add(
            MapBrandLogo(
                "스타벅스",
                "구미 인의점",
                36.1070267,
                128.420661,
                "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
            )
        )

        // 1. 내부저장소에 로고만 저장할 폴더 없으면 만들기
        val path = File("$internalStorage")
        if (!path.exists()) {
            path.mkdirs()
        }

        // 2. 해당 브랜드들 미리 drawable에 다운 시키기 -> 이미 저장되어 있으면 안 하고
        for (store in storeResult) {
            val files = path.listFiles()
            var alreadyStore = false
            for (file in files) {
                if (store.brand == file.name) {
                    alreadyStore = true
                    break
                }
            }
            // 2-1. 브랜드 로고 없다면 추가
            if (!alreadyStore) {
                // 0. storage에 파일 인스턴스를 생성합니다.
                fileName = "${store.brand}.jpg"
                // 1. resizeBitmap
                resizeBitmap(fileName, store.brand)
            }
            // 3. 없다면, 인터넷에 있는 사진을 다운
            // 4. 다운 받고 사이즈를 줄여서 다시 저장
        }

        // 마커 추가하기
        for (store in storeResult) {
            var tempMarker = MapPOIItem()
            tempMarker.apply {
                itemName = store.itemName
                mapPoint = MapPoint.mapPointWithGeoCoord(
                    store.X!!.toDouble(),
                    store.Y!!.toDouble()
                )
                markerType = MapPOIItem.MarkerType.CustomImage
                var filePath = File(internalStorage, "/$fileName")
                customImageBitmap = BitmapFactory.decodeFile(filePath.toString())
                isCustomImageAutoscale = false // 커스텀 마커 이미지 크기 자동 조정
            }
            mapView.addPOIItem(tempMarker)
        }
    }

    // 비트맵 사이즈 비율대로 줄이고 내부 저장소에 저장하는 함수
    fun resizeBitmap(fileName: String, brand: String) {
        val tempFile = File(internalStorage, fileName)
        try {
            tempFile.createNewFile()  // 자동으로 빈 파일을 생성
            val out = FileOutputStream(tempFile) // 파일을 쓸 수 있는 스트림 준비
            var logoUrl = logoImageUrl(brand) // 브랜드에 따라 로고 url 받아오는 함수
            var bitmap = drawableFromUrl(logoUrl).toBitmap() // 로고url에 있는걸 비트맵으로 바꾸는 함수

            // 이미지 크기에 따라 압축률 정하기
            var quality = 0.25
            if (bitmap.width > 2048 && bitmap.height > 2048)
                quality = 0.7
            else if (bitmap.width > 1024 && bitmap.height > 1024)
                quality = 0.4
            // 비트맵 압축률만큼 압축하기
            bitmap = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * quality).toInt(),
                (bitmap.height * quality).toInt(),
                true
            )
            // 비트맵 저장
            saveBitmap(bitmap, tempFile.toString()) // 비트맵 저장
            // 리소스 해제 및 닫기
            out.close()
            bitmap.recycle() // 비트맵은 리소스 많이 먹어서 해제 필수
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
        }
    }

    // 비트맵 저장하는 함수
    fun saveBitmap(bitmap: Bitmap, filePath: String): String {
        var out = FileOutputStream(filePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        return filePath
    }

    // 브랜드에 따라 (서버에 올린?) 로고 이미지 주소 리턴하는 함수
    fun logoImageUrl(brand: String): String {
        return when (brand) {
            "스타벅스" -> "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
            "이디야" -> ""
            else -> ""
        }
    }

    // imgUrl to Drawable
    fun drawableFromUrl(url: String): Drawable {
        val x: Bitmap
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val input: InputStream = connection.getInputStream()
        x = BitmapFactory.decodeStream(input)
        return BitmapDrawable(Resources.getSystem(), x)
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
                }
                isNetworkEnable -> {
                    val location =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    getLongitude = location?.longitude!!
                    getLatitude = location?.latitude!!
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