package com.ssafy.popcon.ui.brandtab

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.FragmentBrandTabBinding
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.StoreRequest
import com.ssafy.popcon.dto.StoreByBrandRequest
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.MapViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

//지도 브랜드탭
class MapBrandtabFragment : Fragment() {
    private lateinit var binding: FragmentBrandTabBinding
    private lateinit var brandAdapter: BrandAdapter
    private lateinit var locationManager: LocationManager
    private var getLongitude: Double = 0.0
    private var getLatitude: Double = 0.0

    private val viewModel: MapViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    lateinit var mainActivity: MainActivity

    override fun onStart() {
        super.onStart()
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBrandTabBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBrandTab()
    }

    //상단 브랜드탭
    private fun setBrandTab() {
        viewModel.getHomeBrand(SharedPreferencesUtil(requireContext()).getUser())
        brandAdapter = BrandAdapter()
        brandAdapter.setItemClickListener(object : BrandAdapter.OnItemClickListener {
            override fun onClick(v: View, brandName: String) {
                getUserLocation()
                Log.d("TAG", "onClick: $brandName")
                val user = SharedPreferencesUtil(requireContext()).getUser()
                if (brandName == "전체") {
                    viewModel.getGifticonByUser(user)
                    viewModel.getStoreInfo(
                        StoreRequest(
                            user.email!!,
                            user.social,
                            getLongitude.toString(), getLatitude.toString()
                        )
                    )
                } else {
                    viewModel.getGifticons(user, brandName)
                    viewModel.getStoreByBrand(
                        StoreByBrandRequest(
                            brandName,
                            user.email!!,
                            user.social,
                            getLongitude.toString(), getLatitude.toString()
                        )
                    )
                }
            }
        })

        viewModel.brandsMap.observe(viewLifecycleOwner) {
            binding.rvBrand.apply {
                adapter = brandAdapter
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            val brands = mutableListOf<Brand>()
            brands.add(Brand("", "전체"))
            for (b in it) {
                brands.add(Brand(b.brand_img, b.brand_name))
            }

            brandAdapter.submitList(brands)
        }
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
}