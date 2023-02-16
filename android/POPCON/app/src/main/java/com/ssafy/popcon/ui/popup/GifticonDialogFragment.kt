package com.ssafy.popcon.ui.popup

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.DialogUseBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.EventObserver
import com.ssafy.popcon.viewmodel.PopupViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory


class GifticonDialogFragment : DialogFragment() {
    private val viewModel: PopupViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    private lateinit var binding: DialogUseBinding
    private var prevIndex = 0
    val TAG = "SHAKE"

    //팝업창 떠있는지 확인하는 변수
    companion object {
        var isShow = false;
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        if (isShow) {
            dismiss()
        } else {
            isShow = true
        }
        super.onAttach(context)

        Log.d(TAG, "onAttach: ")
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

        setBrandTab()
        setViewPager()
    }

    //상품이미지 미리보기, 기프티콘 사용화면
    private fun setViewPager() {
        viewModel.gifticons.observe(viewLifecycleOwner, EventObserver { useList ->
            val previewAdapter =
                PreviewAdapter(
                    childFragmentManager,
                    useList as MutableList<Gifticon>, binding.vpGifticon, binding.vpPreview
                )
            val gifticonViewAdapter = GifticonViewAdapter(
                childFragmentManager,
                useList as MutableList<Gifticon>
            )

            binding.vpGifticon.adapter = gifticonViewAdapter
            binding.vpGifticon.offscreenPageLimit = previewAdapter.sidePreviewCount * 2 + 1

            binding.vpPreview.adapter = previewAdapter

        })

        binding.vpPreview.addOnPageChangeListener(
            OnSyncPageChangeListener(
                binding.vpGifticon,
                binding.vpPreview
            )
        )

        binding.vpGifticon.addOnPageChangeListener(
            OnSyncPageChangeListener(
                binding.vpPreview,
                binding.vpGifticon
            )
        )

        binding.vpPreview.setPageTransformer(
            false
        ) { page, position ->
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
                    val count = binding.vpPreview.adapter!!.count - 4
                    if (count != 1) {
                        if (position >= count) {
                            currentItem = count - 1
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
                }
            })
        }
    }

    //기프티콘 리스트 추가
    private fun setBrandTab() {
        viewModel.brands.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {//근처에 매장 없음
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
    }

    override fun onStop() {
        super.onStop()
        isShow = false
    }
}

