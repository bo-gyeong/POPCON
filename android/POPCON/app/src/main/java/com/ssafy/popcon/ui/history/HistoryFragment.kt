package com.ssafy.popcon.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.FragmentHistoryBinding
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.EventObserver
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.home.GiftconAdapter
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.util.SharedPreferencesUtil
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
        gifticonList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "",
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
                "2023.01.12",
                Badge("사용완료", "#D2D2D2")
            )
        )

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
            historyAdapter = HistoryAdapter()
            historyAdapter.submitList(it)

            binding.rvHistory.apply {
                adapter = historyAdapter
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })*/
    }
}