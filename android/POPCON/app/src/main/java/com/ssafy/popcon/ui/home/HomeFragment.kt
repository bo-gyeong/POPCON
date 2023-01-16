package com.ssafy.popcon.ui.home

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentHomeBinding
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import com.ssafy.popcon.util.ShakeDetector

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var shakeDetector: ShakeDetector
    lateinit var gifticonAdapter: GiftconAdapter
    private lateinit var mainActivity: MainActivity

    override fun onStart() {
        super.onStart()
        mainActivity = activity as MainActivity
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setSensor()
        setGifticonAdapter()
        return binding.root
    }

    //홈 기프티콘 어댑터 설정
    private fun setGifticonAdapter() {
        val gifticonList = mutableListOf<Gifticon>()
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )

        gifticonAdapter = GiftconAdapter()
        binding.rvGifticon.apply {
            adapter = gifticonAdapter
            layoutManager = GridLayoutManager(context, 2)
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        gifticonAdapter.submitList(gifticonList)
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