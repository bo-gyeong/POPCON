package com.ssafy.popcon.ui.popup

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.ssafy.popcon.databinding.DialogUseBinding
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon

class GifticonDialogFragment : DialogFragment() {

    val useList = mutableListOf<Gifticon>()
    private lateinit var binding: DialogUseBinding
    private lateinit var useAdapter: GifticonUseAdapter

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

        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "popup", Toast.LENGTH_SHORT).show()
        setList()
        //setViewPager()
        //setUseAdapter()
        test()
    }

    fun test(){
        val photoPreviewAdapter = PhotoPreviewAdapter(childFragmentManager, useList)
        val gifticonViewAdapter = GifticonViewAdapter(childFragmentManager, useList)

        binding.photoPreviewPager.adapter = photoPreviewAdapter
        binding.photoPreviewPager.addOnPageChangeListener(
            OnSyncPageChangeListener(
                binding.photoViewPager,
                binding.photoPreviewPager
            )
        )

        binding.photoViewPager.adapter = gifticonViewAdapter
        binding.photoViewPager.offscreenPageLimit = photoPreviewAdapter.sidePreviewCount * 2 + 1
        binding.photoViewPager.addOnPageChangeListener(
            OnSyncPageChangeListener(
                binding.photoPreviewPager,
                binding.photoViewPager
            )
        )
    }

    fun setDetail() {

    }


    /*private fun setViewPager() {

        with(binding.viewpagerDialog) {
            adapter = GifticonUseAdapter().apply {
                submitList(useList)
            }

            TabLayoutMediator(binding.viewpagerDialogIndicator, this) { tab, position ->
                tab.view.background = resources.getDrawable(R.drawable.product)
                *//*GlideApp.with(tab.view)
                    .load(useList[position].productUrl)
                    .into(tab.view.findViewById(R.id.iv_product_image))*//*
            }.attach()
        }


    }*/

    //팝업창 아래 기프티콘 사용 목록 어댑터
    /*private fun setUseAdapter() {
        useAdapter = GifticonUseAdapter()

        binding.rvGifticonUse.apply {
            adapter = useAdapter
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        useAdapter.submitList(useList)
    }*/

    private fun setList() {
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
    }
}