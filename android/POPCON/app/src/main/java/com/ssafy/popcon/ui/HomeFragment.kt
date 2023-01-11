package com.ssafy.popcon.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.popcon.databinding.FragmentHomeBinding
import com.ssafy.popcon.ui.common.MainActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onResume() {
        super.onResume()
        val mainActivity = activity as MainActivity
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
}