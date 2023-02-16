package com.ssafy.popcon.ui.brandtab

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.FragmentBrandTabBinding
import com.ssafy.popcon.dto.StoreRequest
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.PopupViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

//팝업화면 브랜드탭
class PopupBrandTabFragment : Fragment() {
    private lateinit var binding: FragmentBrandTabBinding
    lateinit var brandAdapter: BrandAdapter
    private val viewModel: PopupViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    lateinit var mainActivity: MainActivity
    private lateinit var locationManager: LocationManager
    private var getLongitude: Double = 0.0
    private var getLatitude: Double = 0.0

    val TAG = "POPUP BRAND TAB"
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBrandTab()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setBrandTab() {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getUserLocation()
        val user = SharedPreferencesUtil(requireContext()).getUser()
        val storeRequest = StoreRequest(
            user.email!!,
            user.social,
            getLongitude.toString(),
            getLatitude.toString(),
            //"128.64995",//스벅 + 투썸
            //"35.85655"
        )

        viewModel.getBrandByLocation(
            storeRequest,
            SharedPreferencesUtil(requireContext()).getUser()
        )

        brandAdapter = BrandAdapter()
        brandAdapter.setItemClickListener(object : BrandAdapter.OnItemClickListener {
            override fun onClick(v: View, brandName: String) {
                viewModel.getGifticons(SharedPreferencesUtil(requireContext()).getUser(), brandName)
            }
        })

        viewModel.brands.observe(viewLifecycleOwner) {
            binding.rvBrand.apply {
                adapter = brandAdapter
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            brandAdapter.submitList(it)
        }
    }

    //현재위치
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