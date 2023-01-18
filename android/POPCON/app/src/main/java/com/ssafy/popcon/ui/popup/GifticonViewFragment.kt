package com.ssafy.popcon.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.popcon.databinding.ItemGifticonPopupBinding
import com.ssafy.popcon.dto.Gifticon


class GifticonViewFragment : Fragment() {
    private var gifticonInfo: Gifticon? = null
    lateinit var binding : ItemGifticonPopupBinding
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

        binding.gifticon = gifticonInfo
    }

    companion object {
        private const val EXTRA_KEY_GIFTICON_INFO = "extra_key_gifticon_info"
        fun newInstance(photoInfo: Gifticon): GifticonViewFragment {
            val fragment = GifticonViewFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_KEY_GIFTICON_INFO, photoInfo)
            fragment.arguments = args
            return fragment
        }
    }
}