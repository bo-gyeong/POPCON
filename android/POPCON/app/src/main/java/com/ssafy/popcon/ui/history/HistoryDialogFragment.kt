package com.ssafy.popcon.ui.history

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.ssafy.popcon.databinding.DialogHistoryBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.ui.popup.ImageDialogFragment

class HistoryDialogFragment : DialogFragment() {
    private lateinit var binding: DialogHistoryBinding
    private lateinit var history : Gifticon

    override fun onStart() {
        super.onStart()
        GifticonDialogFragment.isShow = true
    }

    override fun onResume() {
        super.onResume()

        //팝업창 크기 설정
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        size.x // 디바이스 가로 길이
        size.y // 디바이스 세로 길이

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogHistoryBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val mArgs = arguments
        history = mArgs!!.getSerializable("history") as Gifticon

        binding.gifticon = history as Gifticon?
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivProductPreview.setOnClickListener {
            val args = Bundle()
            args.putString("originalUrl", history!!.originalUrl)

            val dialogFragment = ImageDialogFragment()
            dialogFragment.arguments = args
            dialogFragment.show(childFragmentManager, "originalUrl")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //기프티콘 상태 업데이트
    }
}