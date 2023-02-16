package com.ssafy.popcon.ui.brandtab

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.FragmentBrandTabBinding
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

//홈화면 브랜드탭
class BrandTabFragment : Fragment() {
    private lateinit var binding: FragmentBrandTabBinding
    private lateinit var brandAdapter: BrandAdapter

    private val viewModel: GifticonViewModel by activityViewModels { ViewModelFactory(requireContext()) }
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

    @RequiresApi(Build.VERSION_CODES.N)
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
                Log.d("TAG", "onClick: $brandName")

                viewModel.getGifticons(SharedPreferencesUtil(requireContext()).getUser(), brandName)
            }
        })

        viewModel.brandsHome.observe(viewLifecycleOwner) {
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
}