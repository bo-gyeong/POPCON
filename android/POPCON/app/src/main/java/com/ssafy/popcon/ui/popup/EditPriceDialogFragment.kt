package com.ssafy.popcon.ui.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.ssafy.popcon.databinding.DialogEditPriceBinding
import com.ssafy.popcon.databinding.DialogUseBinding

class EditPriceDialogFragment : DialogFragment() {
    private lateinit var binding: DialogEditPriceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditPriceBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etPrice.setText("0")

        binding.btn100.setOnClickListener {
            var price = binding.etPrice.text.toString()
            val temp: Int = price.toInt()
            binding.etPrice.setText((temp + 100).toString())
        }

        binding.btn500.setOnClickListener {
            var price = binding.etPrice.text.toString()
            val temp: Int = price.toInt()
            binding.etPrice.setText((temp + 500).toString())
        }

        binding.btn1000.setOnClickListener {
            var price = binding.etPrice.text.toString()
            val temp: Int = price.toInt()
            binding.etPrice.setText((temp + 1000).toString())
        }

        binding.btn5000.setOnClickListener {
            var price = binding.etPrice.text.toString()
            val temp: Int = price.toInt()
            binding.etPrice.setText((temp + 5000).toString())
        }


    }
}