package com.ssafy.popcon.ui.popup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ssafy.popcon.databinding.ItemGifticonSliderBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.popup.PreviewAdapter.PreviewListener

class GifticonPreviewFragment : Fragment() {
    private var gifticonInfo: Gifticon? = null
    private lateinit var binding: ItemGifticonSliderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gifticonInfo = arguments?.getSerializable(EXTRA_KEY_GIFTICON_INFO) as Gifticon
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemGifticonSliderBinding.inflate(layoutInflater, container, false)

        val pos = this.requireArguments().getInt("pos")
        if (pos == 2) {
            binding.bgBlack.isVisible = false
            binding.edgePreview.isVisible = true
        }

        binding.clPreview.setOnClickListener {
            mListener.onSelect(pos)
            mListener.onClick(pos)
        }

        binding.ivProductPreview.clipToOutline = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gifticon = gifticonInfo
    }

    companion object {
        private lateinit var mListener: PreviewListener
        private val EXTRA_KEY_GIFTICON_INFO = "extra_key_gifticon_info"
        fun newInstance(
            gifticonInfo: Gifticon, pos: Int,
            listener: PreviewListener
        ): GifticonPreviewFragment {
            val fragment = GifticonPreviewFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_KEY_GIFTICON_INFO, gifticonInfo)
            args.putInt("pos", pos)
            fragment.arguments = args
            mListener = listener
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}