package com.ssafy.popcon.ui.add

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.popcon.databinding.DialogAddOriginalBinding

class OriginalImgDialogFragment(_imgUri: Uri): DialogFragment() {
    private lateinit var binding:DialogAddOriginalBinding

    private var imgUri:Uri

    init {
        imgUri = _imgUri
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddOriginalBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)

        Glide.with(requireActivity()).load(imgUri).into(binding.ivOriginalImg)
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }
}