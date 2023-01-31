package com.ssafy.popcon.ui.popup

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.viewModelFactory
import com.airbnb.lottie.utils.Utils
import com.ssafy.popcon.databinding.ItemGifticonPopupBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.add.OriginalImgDialogFragment
import com.ssafy.popcon.ui.history.HistoryDialogFragment
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class GifticonViewFragment : Fragment() {
    private var gifticonInfo: Gifticon? = null
    lateinit var binding: ItemGifticonPopupBinding
    private val viewModel: GifticonViewModel by viewModels { ViewModelFactory(requireContext()) }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
        useBtnListener()
    }

    //사용완료 버튼 리스너
    private fun useBtnListener() {
        binding.btnUse.setOnClickListener {
            it.isClickable = false
            viewModel.updateGifticon(gifticonInfo!!)
        }
    }

    //금액권, 아닐 경우 레이아웃 설정
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setLayout() {
        binding.gifticon = gifticonInfo
        if (gifticonInfo?.price == null) {
            binding.btnUse.isVisible = true
            binding.btnPrice.isVisible = false
            binding.tvLeft.isVisible = false
        } else {
            binding.btnUse.isVisible = false
            binding.btnPrice.isVisible = true
            binding.tvLeft.isVisible = true
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
            args.putString("originalUrl", gifticonInfo!!.origin_filepath)

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
}