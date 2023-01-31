package com.ssafy.popcon.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.databinding.DialogHomeGifticonBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.add.AddFragment
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.edit.EditFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.ui.popup.ImageDialogFragment
import com.ssafy.popcon.util.Utils
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class HomeDialogFragment : DialogFragment() {
    private lateinit var binding: DialogHomeGifticonBinding
    private lateinit var barNum: String
    private val viewModel: GifticonViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var mainActivity: MainActivity

    override fun onStart() {
        super.onStart()
        GifticonDialogFragment.isShow = true

        mainActivity = activity as MainActivity
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogHomeGifticonBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val mArgs = arguments
        barNum = mArgs!!.getString("barNum")!!

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setLayout() {
        viewModel.getGifticonByBarcodeNum(barNum)
        viewModel.gifticon.observe(viewLifecycleOwner){ gifticon ->
            setButton(gifticon)
            binding.gifticon = gifticon
            binding.badge = Utils.calDday(gifticon)

            binding.ivProductPreview.setOnClickListener {
                val args = Bundle()
                args.putString("originalUrl", gifticon.origin_filepath)

                val dialogFragment = ImageDialogFragment()
                dialogFragment.arguments = args
                dialogFragment.show(childFragmentManager, "originalUrl")
            }

            //삭제버튼 누르면 삭제요청 하고 다이얼로그 닫기
            binding.btnDelete.setOnClickListener {
                viewModel.deleteGifticon(gifticon.barcodeNum)

                dialog?.dismiss()
            }
        }
    }

    private fun setButton(gifticon : Gifticon) {
        when (gifticon.state) {
            //0:사용가능, 1:사용완료, 2:기간만료
            0 -> {
                //수정 화면으로
                binding.btnUse.setOnClickListener {
                    mainActivity.addFragment(EditFragment())
                }
            }
            1 -> {
                binding.btnUse.text = "되돌리기"

                //사용 가능 상태로 업데이트
                binding.btnUse.setOnClickListener {
                    binding.btnUse.isClickable = false
                    gifticon.state = 0
                    viewModel.updateGifticon(gifticon)
                }
            }
            2 -> {
                //수정 화면으로
                binding.btnUse.setOnClickListener {
                    mainActivity.addFragment(EditFragment())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //기프티콘 상태 업데이트
        /*if (!binding.btnUse.isChecked) {
            viewModel.updateGifticon(gifticon)
        }*/
    }
}