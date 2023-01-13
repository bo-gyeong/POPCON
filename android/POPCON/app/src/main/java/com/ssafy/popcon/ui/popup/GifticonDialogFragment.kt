package com.ssafy.popcon.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.ssafy.popcon.databinding.DialogShakeBinding

class GifticonDialogFragment : DialogFragment() {
    private lateinit var binding: DialogShakeBinding

    //팝업창 떠있는지 확인하는 변수
    companion object {
        var isShow = false;
    }

    override fun onStart() {
        super.onStart()
        isShow = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogShakeBinding.inflate(inflater, container, false)

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