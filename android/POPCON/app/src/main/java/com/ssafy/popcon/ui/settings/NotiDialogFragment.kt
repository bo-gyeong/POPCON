package com.ssafy.popcon.ui.settings

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.DialogSettingsNotiBinding
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.util.SharedPreferencesUtil

class NotiDialogFragment(private val notiListPosition: Int): DialogFragment() {
    private lateinit var binding:DialogSettingsNotiBinding
    private lateinit var user: User

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSettingsNotiBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)

        user = SharedPreferencesUtil(requireParentFragment().requireContext()).getUser()
        showDlgContent(notiListPosition)

        // tvSelectTitle과 width크기 같게
        binding.btnComplete.viewTreeObserver.addOnGlobalLayoutListener (object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val btnParams = binding.btnComplete.layoutParams
                btnParams.width = binding.tvSelectTitle.width
                binding.btnComplete.layoutParams = btnParams

                binding.btnComplete.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        binding.btnComplete.setOnClickListener {
            onClickListener.onClicked(binding.npSelect.value)
            dismiss()
        }

        return builder.create()
    }

    private fun showDlgContent(notiListPosition: Int){
        when(notiListPosition){
            0 -> {
                binding.tvSelectTitle.text = requireContext().resources.getText(R.string.noti_title_first)
                binding.tvSelect.text = requireContext().resources.getText(R.string.first)
                binding.tvSelect.visibility = View.VISIBLE

                binding.npSelect.minValue = 0
                binding.npSelect.maxValue = 30
            }
            1 -> {
                binding.tvSelectTitle.text = requireContext().resources.getText(R.string.noti_title_interval)
                binding.tvSelect.text = requireContext().resources.getText(R.string.interval)
                binding.tvSelect.visibility = View.VISIBLE

                binding.npSelect.minValue = 0
                binding.npSelect.maxValue = user.nday
            }
            2 -> {
                binding.tvSelectTitle.text = requireContext().resources.getText(R.string.noti_title_time)
                binding.tvSelect.visibility = View.GONE

                val timeList = arrayOf("9:00", "13:00", "18:00")
                binding.npSelect.displayedValues = timeList
                binding.npSelect.minValue = 0
                binding.npSelect.maxValue = timeList.size-1
            }
        }
    }

    interface BtnClickListener{
        fun onClicked(selectNum: Int)
    }

    private lateinit var onClickListener: BtnClickListener
    fun setOnClickListener(listener: BtnClickListener){
        onClickListener = listener
    }
}