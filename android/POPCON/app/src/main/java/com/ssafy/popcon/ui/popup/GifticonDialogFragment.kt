package com.ssafy.popcon.ui.popup

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
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ssafy.popcon.databinding.DialogUseBinding
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

class GifticonDialogFragment : DialogFragment() {

    val useList = mutableListOf<Gifticon>()
    private val viewModel: GifticonViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var binding: DialogUseBinding

    //팝업창 떠있는지 확인하는 변수
    companion object {
        var isShow = false;
    }

    override fun onStart() {
        super.onStart()
        isShow = true
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
        binding = DialogUseBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setList()
        setViewPager()
    }

    //상품이미지 미리보기, 기프티콘 사용화면
    private fun setViewPager() {

        val previewAdapter =
            PreviewAdapter(childFragmentManager, useList, binding.vpGifticon, binding.vpPreview)
        val gifticonViewAdapter = GifticonViewAdapter(childFragmentManager, useList)

        binding.vpPreview.adapter = previewAdapter
        binding.vpPreview.addOnPageChangeListener(
            OnSyncPageChangeListener(
                binding.vpGifticon,
                binding.vpPreview
            )
        )

        binding.vpGifticon.adapter = gifticonViewAdapter
        binding.vpGifticon.offscreenPageLimit = previewAdapter.sidePreviewCount * 2 + 1
        binding.vpGifticon.addOnPageChangeListener(
            OnSyncPageChangeListener(
                binding.vpPreview,
                binding.vpGifticon
            )
        )

        binding.vpPreview.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (previewAdapter.getItem(binding.vpPreview.currentItem + 2) is GifticonPreviewFragment) {
                    //새로운거 선택됨
                }
            }
        })
    }

    //기프티콘 리스트 추가
    private fun setList() {
        useList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                null,
                "https://user-images.githubusercontent.com/33195517/213049326-7f10ea87-0094-46ac-9f81-bd136e9ca5f3.png",
                "https://user-images.githubusercontent.com/33195517/212611690-cb2b4fb2-09aa-41ca-851b-c4f51f29153e.png",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        useList.add(
            Gifticon(
                "1234",
                Brand("이디야", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "https://user-images.githubusercontent.com/33195517/212611690-cb2b4fb2-09aa-41ca-851b-c4f51f29153e.png",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        useList.add(
            Gifticon(
                "1234",
                Brand("이디야", ""),
                "아메리카노 T",
                null,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "https://user-images.githubusercontent.com/33195517/212611690-cb2b4fb2-09aa-41ca-851b-c4f51f29153e.png",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        useList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/213049326-7f10ea87-0094-46ac-9f81-bd136e9ca5f3.png",
                "https://user-images.githubusercontent.com/33195517/212611690-cb2b4fb2-09aa-41ca-851b-c4f51f29153e.png",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        useList.add(
            Gifticon(
                "1234",
                Brand("이디야", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "https://user-images.githubusercontent.com/33195517/212611690-cb2b4fb2-09aa-41ca-851b-c4f51f29153e.png",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
        useList.add(
            Gifticon(
                "1234",
                Brand("스타벅스", ""),
                "아메리카노 T",
                30000,
                "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png",
                "https://user-images.githubusercontent.com/33195517/212611690-cb2b4fb2-09aa-41ca-851b-c4f51f29153e.png",
                "2023.01.12",
                Badge("D-23", "#FF7D22FF")
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        isShow = false

        for(gifticon : Gifticon in useList){
            viewModel.updateGifticon(gifticon)
        }
    }
}