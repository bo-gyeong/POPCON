package com.ssafy.popcon.ui.map

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ssafy.popcon.databinding.DialogPresentBinding
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.GetPresentRequest
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.PopconSnackBar
import com.ssafy.popcon.util.MyLocationManager
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.util.Utils
import com.ssafy.popcon.viewmodel.MapViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class PresentDialogFragment : DialogFragment() {
    private lateinit var binding: DialogPresentBinding
    private val viewModel: MapViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    val TAG = "PRESENT DIALOG"
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
        binding = DialogPresentBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        binding.badge = Badge("", "#000000")
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var barNum: String = ""
        viewModel.present.observe(viewLifecycleOwner) { g ->
            barNum = g.barcodeNum
            val gifticon = Gifticon(
                g.barcodeNum,
                g.barcode_filepath ?: "",
                Brand("", g.brandName),
                g.due,
                g.hash,
                g.price,
                g.memo ?: "",
                g.origin_filepath ?: "",
                g.productName,
                g.product_filepath ?: "",
                g.state
            )
            binding.gifticon = gifticon
            binding.badge = Utils.calDday(gifticon)
        }

        binding.btnSend.setOnClickListener {
            //fcm 성공하면 ->
            if (binding.etMessage.text.toString() != "") {
                binding.cvHide.isVisible = false
                val lm = MyLocationManager.getLocationManager(requireContext())
                viewModel.getPresent(
                    GetPresentRequest(
                        barNum,
                        binding.etMessage.text.toString(),
                        MyLocationManager.getLocation(lm)?.longitude.toString(),
                        MyLocationManager.getLocation(lm)?.latitude.toString()
                    ), SharedPreferencesUtil(requireContext()).getUser()
                )
            } else {
                PopconSnackBar.make(view, "감사 인사를 전해보세요!").show()
            }
        }
    }
}

