package com.ssafy.popcon.ui.history

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ssafy.popcon.databinding.DialogHistoryBinding
import com.ssafy.popcon.dto.DeleteRequest
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.PopconSnackBar
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.ui.popup.ImageDialogFragment
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class HistoryDialogFragment : DialogFragment() {
    private lateinit var binding: DialogHistoryBinding
    private lateinit var history: Gifticon
    private val viewModel: GifticonViewModel by viewModels { ViewModelFactory(requireContext()) }


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
            args.putString("originalUrl", history!!.origin_filepath)

            val dialogFragment = ImageDialogFragment()
            dialogFragment.arguments = args
            dialogFragment.show(childFragmentManager, "originalUrl")
        }

        //삭제버튼 누르면 삭제요청 하고 다이얼로그 닫기
        binding.btnDelete.setOnClickListener {
            viewModel.deleteGifticon(DeleteRequest(history.barcodeNum), SharedPreferencesUtil(requireContext()).getUser())
            dialog?.dismiss()
            PopconSnackBar.make(view, "삭제가 완료되었어요").show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //기프티콘 상태 업데이트
        if (!binding.btnUse.isChecked) {
            //viewModel.updateGifticon(history)
        }
    }
}