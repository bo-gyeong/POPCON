package com.ssafy.popcon.ui.popup

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ssafy.popcon.databinding.ItemGifticonPopupBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.UpdateRequest
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class GifticonViewFragment : Fragment() {
    private var gifticonInfo: Gifticon? = null
    lateinit var binding: ItemGifticonPopupBinding
    private val viewModel: GifticonViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gifticonInfo = arguments?.getSerializable(EXTRA_KEY_GIFTICON_INFO) as Gifticon
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemGifticonPopupBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
        useBtnListener()
    }

    private fun makeGifticon(): UpdateRequest {

        return UpdateRequest(
            gifticonInfo!!.barcodeNum,
            gifticonInfo!!.brand!!.brandName,
            gifticonInfo!!.due,
            gifticonInfo!!.memo,
            gifticonInfo!!.price ?: -1,
            gifticonInfo!!.productName,
            SharedPreferencesUtil(requireContext()).getUser().email!!,
            SharedPreferencesUtil(requireContext()).getUser().social,
            gifticonInfo!!.state
        )
    }

    //사용완료 버튼 리스너
    private fun useBtnListener() {
        binding.btnUse.setOnClickListener {
            it.isClickable = false

            gifticonInfo!!.state = 1
            val req = makeGifticon()
            viewModel.updateGifticon(req, SharedPreferencesUtil(requireContext()).getUser())
        }
    }

    //금액권, 아닐 경우 레이아웃 설정
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setLayout() {
        binding.gifticon = gifticonInfo
        Log.d("TAG", "setLayout: $gifticonInfo")
        if (gifticonInfo?.price == -1) {
            binding.btnUse.isVisible = true
            binding.btnPrice.isVisible = false
            binding.tvLeft.isVisible = false
            binding.tvLeftTitle.isVisible = false
        } else {
            binding.btnUse.isVisible = false
            binding.btnPrice.isVisible = true
            binding.tvLeft.isVisible = true
            binding.tvLeftTitle.isVisible = true
        }

        binding.btnPrice.setOnClickListener {
            val args = Bundle()
            args.putSerializable("gifticon", gifticonInfo)

            val dialogFragment = EditPriceDialogFragment()
            dialogFragment.arguments = args
            dialogFragment.show(childFragmentManager, "editPrice")
        }

        binding.tvLeft.text = gifticonInfo!!.price.toString() + " 원 사용가능"

        binding.ivProductPreview.setOnClickListener {
            val args = Bundle()
            args.putString("url", gifticonInfo!!.origin_filepath)

            val dialogFragment = ImageDialogFragment()
            dialogFragment.arguments = args
            dialogFragment.show(childFragmentManager, "originalUrl")
        }

        binding.badge = com.ssafy.popcon.util.Utils.calDday(gifticonInfo!!)
    }

    companion object {
        private const val EXTRA_KEY_GIFTICON_INFO = "extra_key_gifticon_info"
        fun newInstance(gifticon: Gifticon): GifticonViewFragment {
            val fragment = GifticonViewFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_KEY_GIFTICON_INFO, gifticon)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}