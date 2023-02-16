package com.ssafy.popcon.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.databinding.FragmentHomeBinding
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.EventObserver
import com.ssafy.popcon.ui.brandtab.BrandTabFragment
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.common.PopconSnackBar
import com.ssafy.popcon.ui.history.HistoryDialogFragment
import com.ssafy.popcon.ui.history.HistoryFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import com.ssafy.popcon.ui.popup.GifticonViewAdapter
import com.ssafy.popcon.ui.settings.SettingsFragment
import com.ssafy.popcon.util.ShakeDetector
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var shakeDetector: ShakeDetector
    lateinit var gifticonAdapter: GiftconAdapter
    private val viewModel: GifticonViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    private lateinit var mainActivity: MainActivity
    val TAG = "HOME"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isShow = false
        mainActivity = activity as MainActivity
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        setSensor()
        mainActivity.hideBottomNav(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        mainActivity.updateStatusBarColor("#FFFFFF")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: ")
        binding.user = SharedPreferencesUtil(requireContext()).getUser()
        binding.viewModel = viewModel

        setGifticonAdapter()
        openGifticonDialog()

        binding.btnHistory.setOnClickListener {
            mainActivity.addFragment(HistoryFragment())
        }
    }

    private fun openGifticonDialog() {
        viewModel.openGifticonDialogEvent.observe(viewLifecycleOwner, EventObserver {
            Log.d(TAG, "openGifticonDialog: $it")
            val args = Bundle()
            args.putSerializable("gifticon", it)
            val dialogFragment = HomeDialogFragment()
            dialogFragment.arguments = args
            dialogFragment.show(childFragmentManager, "popup")
        })
    }


    //홈 기프티콘 어댑터 설정
    private fun setGifticonAdapter() {
        gifticonAdapter = GiftconAdapter(viewModel)
        viewModel.getGifticonByUser(SharedPreferencesUtil(requireContext()).getUser())
        viewModel.gifticons.observe(viewLifecycleOwner) {
            binding.tvNoGifticon.isVisible = it.isEmpty()

            binding.rvGifticon.apply {
                adapter = gifticonAdapter
                layoutManager = GridLayoutManager(context, 2)
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            gifticonAdapter.apply {
                submitList(it)
            }
        }
    }

    //홈화면 켜지면 센서 설정
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
