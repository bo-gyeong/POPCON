package com.ssafy.popcon.ui.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ssafy.popcon.databinding.DialogEditPriceBinding
import com.ssafy.popcon.databinding.DialogUseBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.UpdateRequest
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.util.Utils
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.PopupViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class EditPriceDialogFragment : DialogFragment() {
    private lateinit var binding: DialogEditPriceBinding
    private lateinit var gifticon: Gifticon
    private val viewModel: PopupViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditPriceBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val mArgs = arguments
        gifticon = mArgs!!.getSerializable("gifticon") as Gifticon

        Log.d("gifticon", "onCreateView: $gifticon")
        binding.gifticon = gifticon as Gifticon?

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        priceBtnCilckListener()
    }

    //가격버튼
    private fun priceBtnCilckListener() {

        binding.btn500.setOnClickListener {
            var price = "0"
            if (binding.etPrice.text.toString() != "") {
                price = binding.etPrice.text.toString()
            }
            val temp: Int = price.toInt()
            binding.etPrice.setText((temp + 500).toString())
        }

        binding.btn1000.setOnClickListener {
            var price = "0"
            if (binding.etPrice.text.toString() != "") {
                price = binding.etPrice.text.toString()
            }
            val temp: Int = price.toInt()
            binding.etPrice.setText((temp + 1000).toString())
        }

        binding.btn5000.setOnClickListener {
            var price = "0"
            if (binding.etPrice.text.toString() != "") {
                price = binding.etPrice.text.toString()
            }
            val temp: Int = price.toInt()
            binding.etPrice.setText((temp + 5000).toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.updateGifticon(setGifticon(), SharedPreferencesUtil(requireContext()).getUser())
    }

    private fun setGifticon(): UpdateRequest {
        gifticon.price = gifticon.price!!.toInt() - binding.etPrice.text.toString().toInt()

        return UpdateRequest(
            gifticon.barcodeNum,
            gifticon.brand!!.brandName,
            gifticon.due,
            gifticon.memo,
            gifticon.price ?: -1,
            gifticon.productName,
            SharedPreferencesUtil(requireContext()).getUser().email!!,
            SharedPreferencesUtil(requireContext()).getUser().social,
            gifticon.state
        )
    }
}