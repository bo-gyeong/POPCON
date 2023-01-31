package com.ssafy.popcon.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.FragmentHistoryBinding
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var mainActivity: MainActivity
    lateinit var historyAdapter: HistoryAdapter
    private val viewModel: GifticonViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onStart() {
        super.onStart()
        mainActivity = activity as MainActivity
        GifticonDialogFragment.isShow = true
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHistoryAdapter()
    }

    private fun setHistoryAdapter() {
        val gifticonList = mutableListOf<Gifticon>()
        makeList(gifticonList)

        //viewModel.getHistory(SharedPreferencesUtil(requireContext()).getUser().email!!)
        historyAdapter = HistoryAdapter(HistoryAdapter.HistoryListener { history ->
            Log.d("TAG", "setHistoryAdapter: $history")
            val args = Bundle()
            args.putSerializable("history", history)

            val dialogFragment = HistoryDialogFragment()
            dialogFragment.arguments = args
            dialogFragment.show(childFragmentManager, "popup")
        })

        historyAdapter.submitList(gifticonList)

        binding.rvHistory.apply {
            adapter = historyAdapter
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        /*viewModel.history.observe(viewLifecycleOwner, Observer {
            historyAdapter = HistoryAdapter(HistoryAdapter.HistoryListener { history ->
            Log.d("TAG", "setHistoryAdapter: $history")
            val args = Bundle()
            args.putSerializable("history", history)

            val dialogFragment = HistoryDialogFragment()
            dialogFragment.arguments = args
            dialogFragment.show(childFragmentManager, "popup")
        })
            historyAdapter.submitList(it)

            binding.rvHistory.apply {
                adapter = historyAdapter
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })*/
    }

    override fun onDestroy() {
        super.onDestroy()
        GifticonDialogFragment.isShow = false
    }
}

/*
* gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
                "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
                "2023.01.12",
                Badge("사용완료", "#D2D2D2")
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
                "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
                "2023.01.12",
                Badge("사용완료", "#D2D2D2")
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
                "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
                "2023.01.12",
                Badge("사용완료", "#D2D2D2")
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
                "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
                "2023.01.12",
                Badge("사용완료", "#D2D2D2")
            )
        )
        * */
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
            "https://user-images.githubusercontent.com/33195517/214759061-e4fad749-656d-4feb-acf0-f1f579cef0b0.png"
        )
    )
    //
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
            "https://user-images.githubusercontent.com/33195517/214758856-5066c400-9544-4501-a80f-00e0ebceba74.png"
        )
    )
}