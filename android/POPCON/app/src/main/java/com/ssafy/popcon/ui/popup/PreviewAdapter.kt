package com.ssafy.popcon.ui.popup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ssafy.popcon.dto.Gifticon


class PhotoPreviewAdapter(
    fm: FragmentManager?,
    val sidePreviewCount: Int,
    photoInfos: List<Gifticon>
) :
    FragmentPagerAdapter(fm!!) {
    private val photoInfos: List<Gifticon>

    constructor(fm: FragmentManager?, photoInfos: List<Gifticon>) : this(
        fm,
        DEFAULT_SIDE_PREVIEW_COUNT,
        photoInfos
    )

    init {
        this.photoInfos = photoInfos
    }

    override fun getItem(position: Int): Fragment {
        return if (isDummy(position)) {
            DummyPreviewFragment()
        } else {
            GifticonPreviewFragment.newInstance(photoInfos[getRealPosition(position)])
        }
    }

    private fun isDummy(position: Int): Boolean {
        return position < sidePreviewCount || position > photoInfos.size - 1 + sidePreviewCount
    }

    private fun getRealPosition(position: Int): Int {
        return position - sidePreviewCount
    }

    override fun getCount(): Int {
        return photoInfos.size + sidePreviewCount * 2
    }

    override fun getPageWidth(position: Int): Float {
        return 1.0f / elementsPerPage
    }

    private val elementsPerPage: Int
        private get() = sidePreviewCount * 2 + 1

    companion object {
        private const val DEFAULT_SIDE_PREVIEW_COUNT = 3
    }
}