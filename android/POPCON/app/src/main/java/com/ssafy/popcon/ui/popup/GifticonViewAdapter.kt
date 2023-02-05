package com.ssafy.popcon.ui.popup

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ssafy.popcon.dto.Gifticon


class GifticonViewAdapter(fm: FragmentManager, private val gifticons: List<Gifticon>) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return GifticonViewFragment.newInstance(gifticons[position])
    }

    override fun getCount(): Int {
        return gifticons.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }
}