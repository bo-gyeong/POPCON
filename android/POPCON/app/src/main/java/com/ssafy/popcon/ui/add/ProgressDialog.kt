package com.ssafy.popcon.ui.add

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.DialogProgressBinding

class ProgressDialog(private val cancel: Boolean):DialogFragment() {
    private lateinit var binding:DialogProgressBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogProgressBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context, R.style.TransparentDialog)
        builder.setView(binding.root)
        isCancelable = cancel

        Glide.with(requireContext()).load(R.raw.pop).into(binding.ivLoading)

        return builder.create()
    }
}