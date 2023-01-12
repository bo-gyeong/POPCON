package com.ssafy.popcon.ui.popup

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.popcon.databinding.DialogShakeBinding

class GifticonPopup : Fragment() {
    private lateinit var binding: DialogShakeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogShakeBinding.inflate(inflater, container, false)

        return binding.root
    }
}