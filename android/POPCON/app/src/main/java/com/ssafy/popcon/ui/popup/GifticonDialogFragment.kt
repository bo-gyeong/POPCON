package com.ssafy.popcon.ui.popup

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.ssafy.popcon.databinding.DialogShakeBinding
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.util.ShakeDetector

class GifticonDialogFragment : DialogFragment() {
    private lateinit var binding: DialogShakeBinding

    companion object {
        var isShow = false;
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogShakeBinding.inflate(inflater, container, false)

        isShow = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "popup", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        isShow = false
    }
}