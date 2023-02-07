package com.ssafy.popcon.ui.add

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.DialogMmsBinding
import com.ssafy.popcon.ui.common.MainActivity

class MMSDialog: DialogFragment() {
    private lateinit var binding: DialogMmsBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogMmsBinding.inflate(layoutInflater)
        mainActivity = MainActivity.getInstance()!!

        val builder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        builder.setView(binding.root)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnGoToAdd.setOnClickListener {
            mainActivity.addFragmentFromMMS(AddFragment())
            dismiss()
        }

        return builder.create()
    }
}