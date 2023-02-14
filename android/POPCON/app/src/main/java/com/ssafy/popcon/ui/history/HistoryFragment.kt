package com.ssafy.popcon.ui.history

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentHistoryBinding
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.UserDeleteRequest
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class HistoryFragment : Fragment() {
    val TAG = "HISTORY"
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var mainActivity: MainActivity
    lateinit var historyAdapter: HistoryAdapter
    private val viewModel: GifticonViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    override fun onStart() {
        super.onStart()
        mainActivity = activity as MainActivity
        GifticonDialogFragment.isShow = true
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.btnHistory.backgroundTintList =
            requireContext().resources.getColorStateList(R.color.history_btn)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHistoryAdapter()
    }

    private fun setHistoryAdapter() {
        val user = SharedPreferencesUtil(requireContext()).getUser()
        viewModel.getHistory(UserDeleteRequest(user.email!!, user.social))

        historyAdapter = HistoryAdapter(HistoryAdapter.HistoryListener { history ->
            val args = Bundle()
            args.putString("history", history.barcodeNum)

            val dialogFragment = HistoryDialogFragment()
            dialogFragment.arguments = args
            dialogFragment.show(childFragmentManager, "popup")
        })

        viewModel.history.observe(viewLifecycleOwner) {
            historyAdapter.submitList(it)
        }

        binding.rvHistory.apply {
            adapter = historyAdapter
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        GifticonDialogFragment.isShow = false
    }
}