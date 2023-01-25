package com.ssafy.popcon.ui.map

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.popup.GifticonPreviewFragment.Companion.newInstance
import com.ssafy.popcon.ui.popup.GifticonViewFragment.Companion.newInstance

private const val TAG = "NoticeAdapter_싸피"


class MapGifticonAdpater(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    //    TODO  numPages를 서버에서 받은거만큼 사이즈 늘려주고
    private var numPages = 3

    override fun getItemCount(): Int = numPages

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> {
                MapGifticonFragment.newInstance("0", "")
            }
            1 -> {
                MapGifticonFragment.newInstance("1", "")
            }
            else -> {
                MapGifticonFragment.newInstance("2", "")
            }
        }
    }
}