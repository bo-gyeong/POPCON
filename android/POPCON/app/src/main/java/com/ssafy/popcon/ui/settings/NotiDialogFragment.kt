package com.ssafy.popcon.ui.settings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.ssafy.popcon.databinding.DialogSettingsNotiBinding

class NotiDialogFragment: DialogFragment() {
    private lateinit var binding:DialogSettingsNotiBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSettingsNotiBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)

        binding.tvSelect.visibility = View.GONE

        return builder.create()
    }
}