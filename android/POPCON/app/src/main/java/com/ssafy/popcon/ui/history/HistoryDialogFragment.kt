package com.ssafy.popcon.ui.history

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ssafy.popcon.databinding.DialogHistoryBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.ui.common.PopconSnackBar
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.ui.popup.ImageDialogFragment
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.util.Utils
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class HistoryDialogFragment : DialogFragment() {
    private lateinit var binding: DialogHistoryBinding
    private lateinit var history: String
    private val viewModel: GifticonViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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
        history = mArgs!!.getString("history").toString()
        binding.badge = Badge("", "#000000")

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getGifticonByBarcodeNum(history)
        viewModel.gifticon.observe(viewLifecycleOwner){ g->
            val gifticon = Gifticon(
                g.barcodeNum,
                g.barcode_filepath?:"",
                Brand("", g.brandName),
                g.due,
                g.hash,
                g.price,
                g.memo?:"",
                g.origin_filepath?:"",
                g.productName,
                g.product_filepath?:"",
                g.state
            )

            binding.gifticon = gifticon
            binding.badge = Utils.calDday(gifticon)

            binding.ivProductPreview.setOnClickListener {
                val args = Bundle()
                args.putString("originalUrl", g.origin_filepath)

                val dialogFragment = ImageDialogFragment()
                dialogFragment.arguments = args
                dialogFragment.show(childFragmentManager, "originalUrl")
            }

            //되돌리기
            binding.btnUse.setOnClickListener {
                binding.btnUse.isClickable = false
                val req = UpdateRequest(g.barcodeNum, g.brandName, g.due, g.memo ?: "", g.price ?: -1, g.productName, SharedPreferencesUtil(requireContext()).getUser().email!!, SharedPreferencesUtil(requireContext()).getUser().social, 0)
                viewModel.updateGifticon(req, SharedPreferencesUtil(requireContext()).getUser())

                dialog?.dismiss()
            }
        }

        //삭제버튼 누르면 삭제요청 하고 다이얼로그 닫기
        binding.btnDelete.setOnClickListener {
            viewModel.deleteGifticon(DeleteRequest(history), SharedPreferencesUtil(requireContext()).getUser())
            dialog?.dismiss()
            PopconSnackBar.make(view, "삭제가 완료되었어요").show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //기프티콘 상태 업데이트

    }
}