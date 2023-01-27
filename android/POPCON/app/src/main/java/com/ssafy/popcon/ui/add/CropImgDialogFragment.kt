package com.ssafy.popcon.ui.add

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.DialogAddCropChkBinding
import com.ssafy.popcon.dto.GifticonImg

class CropImgDialogFragment(_gifticonImg: GifticonImg, private val clickFromCv:String): DialogFragment() {
    private lateinit var binding:DialogAddCropChkBinding

    private var gifticonImg:GifticonImg

    init {
        gifticonImg = _gifticonImg
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddCropChkBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        builder.setView(binding.root)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding.ivCropImg.clipToOutline = true

        binding.gifticonImg = gifticonImg
        binding.btnCancel.setOnClickListener{
            dismiss()
        }
        binding.btnRecrop.setOnClickListener {
            onClickListener.onClicked(clickFromCv)
            dismiss()
        }

        return builder.create()
    }

    interface BtnClickListener{
        fun onClicked(fromCv:String)
    }

    private lateinit var onClickListener: BtnClickListener

    fun setOnClickListener(listener: BtnClickListener){
        onClickListener = listener
    }
}