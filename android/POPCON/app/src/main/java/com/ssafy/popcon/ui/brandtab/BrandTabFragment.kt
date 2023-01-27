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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.FragmentBrandTabBinding
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.EventObserver
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.history.HistoryFragment
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.UserViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory
import kotlin.streams.toList

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

        viewModel.openHistoryEvent.observe(viewLifecycleOwner, EventObserver {
            mainActivity.addFragment(HistoryFragment())
        })

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBrandTab()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setBrandTab() {
        var brands = mutableListOf<Brand>()

        viewModel.gifticons.observe(viewLifecycleOwner){
            brands.add(Brand("", "전체"))
            brands.addAll(getBrands(it))
            brands.add(Brand("", "히스토리"))

            brandAdapter = BrandAdapter(viewModel, SharedPreferencesUtil(requireContext()).getUser())

            binding.rvBrand.apply {
                adapter = brandAdapter
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            brandAdapter.apply {
                submitList(brands)
                stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun getBrands(gifticons : List<Gifticon>): List<Brand> {
    Log.d("TAG", "getBrands: ${gifticons}")
    Log.d("TAG", "getBrands: ${gifticons.stream().map { gc -> gc.brand }?.distinct()!!.toList()}")

    var brandSet : Set<String> = setOf()
    for(gifticon : Gifticon in gifticons){

    }
    return gifticons.stream().map { gc -> gc.brand }?.distinct()!!.toList()
}