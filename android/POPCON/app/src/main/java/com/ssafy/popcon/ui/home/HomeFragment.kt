package com.ssafy.popcon.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
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

    override fun onStart() {
        super.onStart()
        mainActivity = activity as MainActivity
    }

    override fun onResume() {
        super.onResume()
        setSensor()
        mainActivity.hideBottomNav(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            args.putSerializable("barNum", it)

            val dialogFragment = HomeDialogFragment()
            dialogFragment.arguments = args
            dialogFragment.show(childFragmentManager, "popup")
        })
    }

    //홈 기프티콘 어댑터 설정
    private fun setGifticonAdapter() {
        Log.d(TAG, "setGifticonAdapter: ${ApplicationClass.sharedPreferencesUtil.accessToken}")
        viewModel.getGifticonByUser(SharedPreferencesUtil(requireContext()).getUser())
        viewModel.gifticons.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.tvNoGifticon.isVisible = true
            } else {
                binding.tvNoGifticon.isVisible = false

                gifticonAdapter = GiftconAdapter(viewModel)

                binding.rvGifticon.apply {
                    adapter = gifticonAdapter
                    layoutManager = GridLayoutManager(context, 2)
                    adapter!!.stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }

                gifticonAdapter.submitList(it)
            }
        }

        //테스트용
        /*gifticonAdapter = GiftconAdapter()
        binding.rvGifticon.apply {
            adapter = gifticonAdapter
            layoutManager = GridLayoutManager(context, 2)
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        val gifticonList = mutableListOf<Gifticon>()
        makeList(gifticonList)
        gifticonAdapter.submitList(gifticonList)*/
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

    //테스트용 기프티콘 리스트
    private fun makeList(gifticonList: MutableList<Gifticon>) {
        gifticonList.add(
            Gifticon(
                "1234-1234",
                "https://user-images.githubusercontent.com/33195517/214758057-5768a3d2-a441-4ba3-8f68-637143daceb3.png",
                Brand(
                    "스타벅스",
                    "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
                ),

                "2023-01-29 00:00:00.000000",
                -1,
                5000,
                "유라",
                "https://user-images.githubusercontent.com/33195517/214758165-4e216728-cade-45ff-a635-24599384997c.png",
                "아메리카노 T",
                "https://user-images.githubusercontent.com/33195517/214759061-e4fad749-656d-4feb-acf0-f1f579cef0b0.png",
                0
            )
        )
        gifticonList.add(
            Gifticon(
                "1234-1234",
                "https://user-images.githubusercontent.com/33195517/214758057-5768a3d2-a441-4ba3-8f68-637143daceb3.png",
                Brand(
                    "스타벅스",
                    "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
                ),
                "2023-02-10 00:00:00.000000",
                -1,
                30000,
                "유라",
                "https://user-images.githubusercontent.com/33195517/214758165-4e216728-cade-45ff-a635-24599384997c.png",
                "아이스 카페 라떼 T",
                "https://user-images.githubusercontent.com/33195517/214758856-5066c400-9544-4501-a80f-00e0ebceba74.png",
                1
            )
        )
        Gifticon(
            "1234-1234",
            "https://user-images.githubusercontent.com/33195517/214758057-5768a3d2-a441-4ba3-8f68-637143daceb3.png",
            Brand(
                "스타벅스",
                "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
            ),

            "2023-01-29 00:00:00.000000",
            -1,
            5000,
            "유라",
            "https://user-images.githubusercontent.com/33195517/214758165-4e216728-cade-45ff-a635-24599384997c.png",
            "아메리카노 T",
            "https://user-images.githubusercontent.com/33195517/214759061-e4fad749-656d-4feb-acf0-f1f579cef0b0.png",
            2
        )
        gifticonList.add(
            Gifticon(
                "1234-1234",
                "https://user-images.githubusercontent.com/33195517/214758057-5768a3d2-a441-4ba3-8f68-637143daceb3.png",
                Brand(
                    "이디야",
                    "https://user-images.githubusercontent.com/33195517/211949184-c6e4a8e1-89a2-430c-9ccf-4d0a20546c14.png"
                ),
                "2023-02-10 00:00:00.000000",
                -1,
                30000,
                "유라",
                "https://user-images.githubusercontent.com/33195517/214758165-4e216728-cade-45ff-a635-24599384997c.png",
                "아이스 카페 라떼 T",
                "https://user-images.githubusercontent.com/33195517/214758856-5066c400-9544-4501-a80f-00e0ebceba74.png",
                0
            )
        )
    }
}
