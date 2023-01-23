package com.ssafy.popcon.ui.add

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.DialogAddOriginalBinding
import com.ssafy.popcon.dto.GifticonImg

class OriginalImgDialogFragment(_gifticonImg:GifticonImg): DialogFragment() {
    private lateinit var binding:DialogAddOriginalBinding

    private var gifticonImg:GifticonImg

    init {
        gifticonImg = _gifticonImg
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddOriginalBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        builder.setView(binding.root)

        binding.gifticonImg = gifticonImg
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }
}