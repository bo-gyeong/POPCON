package com.ssafy.popcon.ui.home.brandtab

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.FragmentBrandTabBinding
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.EventObserver
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.history.HistoryFragment
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.util.Utils
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.UserViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory
import kotlin.streams.toList

class BrandTabFragment : Fragment() {
    private lateinit var binding: FragmentBrandTabBinding
    private lateinit var brandAdapter: BrandAdapter
    var brands = mutableListOf<Brand>()

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

        viewModel.openHistoryEvent.observe(viewLifecycleOwner, EventObserver {
            mainActivity.addFragment(HistoryFragment())
        })

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        //setBrandTab()
    }

    private fun init() {
        brands.clear()
        brands.add(Brand("", "전체"))
        brands.add(Brand("", "히스토리"))

        brandAdapter =
            BrandAdapter(viewModel, SharedPreferencesUtil(requireContext()).getUser())

        binding.rvBrand.apply {
            adapter = brandAdapter
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        brandAdapter.submitList(brands)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setBrandTab() {
        viewModel.allGifticons.observe(viewLifecycleOwner) {
            //Log.d("TAG", "setBrandTab: $it")
            brands.clear()
            brands.add(Brand("", "전체"))
            //brands.addAll(Utils.getBrands(it))
            brands.add(Brand("", "히스토리"))

            brandAdapter =
                BrandAdapter(viewModel, SharedPreferencesUtil(requireContext()).getUser())

            binding.rvBrand.apply {
                adapter = brandAdapter
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            brandAdapter.submitList(brands)
        }
    }
}