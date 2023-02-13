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
    private lateinit var fcmToken: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSettingsNotiBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)

        user = SharedPreferencesUtil(requireParentFragment().requireContext()).getUser()
        fcmToken = SharedPreferencesUtil(requireParentFragment().requireContext()).getFCMToken()
        userInit()
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
            onClickListener.onClicked(notiListPosition, binding.npSelect.value)
            dismiss()
        }

        return builder.create()
    }

    // user 초기화
    private fun userInit(){
        val shardPreference = SharedPreferencesUtil(requireContext()).preferences
        user = User(
            user.email,
            user.social,
            shardPreference.getInt("noti_first", 1),
            shardPreference.getInt("alarm", 1),
            shardPreference.getInt("manner_temp", 1),
            shardPreference.getInt("noti_interval", 1),
            shardPreference.getInt("noti_time", 1),
            fcmToken
        )
    }

    private fun showDlgContent(notiListPosition: Int){
        when(notiListPosition){
            0 -> {
                binding.tvSelectTitle.text = requireContext().resources.getText(R.string.noti_title_first)
                binding.tvSelect.text = requireContext().resources.getText(R.string.first)
                binding.tvSelect.visibility = View.VISIBLE

                binding.npSelect.minValue = 0
                binding.npSelect.maxValue = 30
                binding.npSelect.value = user.nday
            }
            1 -> {
                binding.tvSelectTitle.text = requireContext().resources.getText(R.string.noti_title_interval)
                binding.tvSelect.text = requireContext().resources.getText(R.string.interval)
                binding.tvSelect.visibility = View.VISIBLE

                binding.npSelect.minValue = 1
                binding.npSelect.maxValue = user.nday
                binding.npSelect.value = user.term
            }
            2 -> {
                binding.tvSelectTitle.text = requireContext().resources.getText(R.string.noti_title_time)
                binding.tvSelect.visibility = View.GONE

                val timeList = arrayOf(
                    this.resources.getText(R.string.time_9) as String,
                    this.resources.getText(R.string.time_13) as String,
                    this.resources.getText(R.string.time_18) as String
                )
                binding.npSelect.displayedValues = timeList
                binding.npSelect.minValue = 0
                binding.npSelect.maxValue = timeList.size-1
                binding.npSelect.value = user.timezone
            }
        }
    }

    interface BtnClickListener{
        fun onClicked(selectPos: Int, selectValue:Int)
    }

    private lateinit var onClickListener: BtnClickListener
    fun setOnClickListener(listener: BtnClickListener){
        onClickListener = listener
    }
}