package com.ssafy.popcon.ui.popup

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager.PageTransformer
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.DialogUseBinding
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.PopupViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory


class GifticonDialogFragment : DialogFragment() {
    private val viewModel: PopupViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    private lateinit var binding: DialogUseBinding
    private var prevIndex = 0

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

    @RequiresApi(Build.VERSION_CODES.N)
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
    }

    //상품이미지 미리보기, 기프티콘 사용화면
    private fun setViewPager(useList: List<Gifticon>) {
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

        binding.vpPreview.setPageTransformer(
            false
        ) { page, position ->
            Log.d("TAG", "transformPage: $position")
            page.translationX = position * -40
        }

        binding.vpPreview.apply {
            addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    if (position >= useList.size) {
                        currentItem = useList.size - 1
                        prevIndex = currentItem - 1
                    }

                    if (currentItem != prevIndex) {

                        val v: View = binding.vpPreview.getChildAt(currentItem)

                        v.findViewById<ImageView>(R.id.bg_black).isVisible = false
                        v.findViewById<ImageView>(R.id.edge_preview).isVisible = true

                        val oldV: View = binding.vpPreview.getChildAt(prevIndex)
                        oldV.findViewById<ImageView>(R.id.bg_black).isVisible = true
                        oldV.findViewById<ImageView>(R.id.edge_preview).isVisible = false

                        prevIndex = currentItem
                    }
                }
            })
        }
    }

    //기프티콘 리스트 추가
    private fun setList() {
        viewModel.brands.observe(viewLifecycleOwner) {
            viewModel.getGifticons(
                SharedPreferencesUtil(requireContext()).getUser(),
                it[0].brandName
            )

            if (it.size == 0) {//근처에 매장 없음
                binding.cvBrandTab.isVisible = false
                binding.vpGifticon.isVisible = false
                binding.vpPreview.isVisible = false
                binding.tvNoBrand.isVisible = true
            } else if (it.size >= 2) {//2개면 브랜드탭 보여줌
                binding.cvBrandTab.isVisible = true
                binding.vpGifticon.isVisible = true
                binding.vpPreview.isVisible = true
                binding.tvNoBrand.isVisible = false
            } else {//1개면 브랜드탭 숨김
                binding.cvBrandTab.isVisible = false
                binding.vpGifticon.isVisible = true
                binding.vpPreview.isVisible = true
                binding.tvNoBrand.isVisible = false
            }
        }

        viewModel.gifticons.observe(viewLifecycleOwner) {
            setViewPager(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isShow = false

        /*for(gifticon : Gifticon in useList){

          viewModel.updateGifticon(gifticon)
        }*/
    }
}


/*useList.add(
    Gifticon(
        "1234",
        Brand("스타벅스", ""),
        "아메리카노 T",
        null,
        "https://user-images.githubusercontent.com/33195517/213049326-7f10ea87-0094-46ac-9f81-bd136e9ca5f3.png",
        "https://user-images.githubusercontent.com/33195517/212611690-cb2b4fb2-09aa-41ca-851b-c4f51f29153e.png",
        "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
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
        "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
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
        "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
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
        "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
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
        "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
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
        "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
        "2023.01.12",
        Badge("D-23", "#FF7D22FF")
    )
)*/