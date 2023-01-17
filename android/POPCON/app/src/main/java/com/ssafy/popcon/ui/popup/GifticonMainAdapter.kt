package com.ssafy.popcon.ui.popup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ssafy.popcon.dto.Gifticon


class GifticonViewAdapter(fm: FragmentManager?, photoInfos: List<Gifticon>) :
    FragmentPagerAdapter(fm!!) {
    private val gifticons: List<Gifticon>

    init {
        this.gifticons = photoInfos
    }

    override fun getItem(position: Int): Fragment {
        return GifticonViewFragment.newInstance(gifticons[position])
    }

    override fun getCount(): Int {
        return gifticons.size
    }
}