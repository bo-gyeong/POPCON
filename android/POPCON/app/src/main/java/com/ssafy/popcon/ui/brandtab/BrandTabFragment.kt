package com.ssafy.popcon.ui.brandtab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.FragmentBrandTabBinding
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.ui.common.EventObserver
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.history.HistoryFragment
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.UserViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class BrandTabFragment : Fragment() {
    private lateinit var binding: FragmentBrandTabBinding
    private lateinit var brandAdapter: BrandAdapter
    private val viewModel: GifticonViewModel by viewModels { ViewModelFactory(requireContext()) }
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

    fun setBrandTab(brands: List<Brand>) {
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

/*brandList.add(
           Brand(
               "전체",
               ""
           )
       )
       brandList.add(
           Brand(
               "스타벅스",
               "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
           )
       )
       brandList.add(
           Brand(
               "이디야",
               "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
           )
       )
       brandList.add(
           Brand(
               "이디야",
               "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
           )
       )
       brandList.add(
           Brand(
               "이디야",
               "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
           )
       )
       brandList.add(
           Brand(
               "이디야",
               "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
           )
       )
       brandList.add(
           Brand(
               "이디야",
               "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
           )
       )
       brandList.add(
           Brand(
               "히스토리",
               ""
           )
       )*/