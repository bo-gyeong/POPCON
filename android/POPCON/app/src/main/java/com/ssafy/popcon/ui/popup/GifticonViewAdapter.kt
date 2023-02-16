package com.ssafy.popcon.ui.popup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ssafy.popcon.dto.Gifticon


class GifticonViewAdapter(fm: FragmentManager, private val gifticons: MutableList<Gifticon>) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return GifticonViewFragment.newInstance(gifticons[position])
    }

    override fun getCount(): Int {
        return gifticons.size
    }
}