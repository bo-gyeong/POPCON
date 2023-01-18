package com.ssafy.popcon.ui.popup

import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

class OnSyncPageChangeListener(
    private val syncToViewPager: ViewPager,
    private val syncWithViewPager: ViewPager
) : OnPageChangeListener {
    private var scrollState = ViewPager.SCROLL_STATE_IDLE

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (scrollState != ViewPager.SCROLL_STATE_IDLE) {
            val ratio = calculateRatioForPosition(position)
            val scrollX = syncWithViewPager.scrollX.toFloat()
            val scrollY = syncWithViewPager.scrollY.toFloat()
            syncToViewPager.scrollTo((scrollX * ratio).toInt(), scrollY.toInt())
        }
    }

    private fun calculateRatioForPosition(position: Int): Float {
        val syncToViewPagerWidth = syncToViewPager.width.toFloat()
        val syncWithViewPagerWidth = syncWithViewPager.width.toFloat()
        val syncToViewPagerElementWeight = syncToViewPager.adapter!!.getPageWidth(position)
        val syncWithViewPagerElementWeight = syncWithViewPager.adapter!!.getPageWidth(position)
        val syncToViewPagerElementsCount = 1.0f / syncToViewPagerElementWeight
        val syncWithViewPagerElementsCount = 1.0f / syncWithViewPagerElementWeight
        val syncToViewPagerElementWidth = syncToViewPagerWidth / syncToViewPagerElementsCount
        val syncWithViewPagerElementWidth = syncWithViewPagerWidth / syncWithViewPagerElementsCount
        return syncToViewPagerElementWidth / syncWithViewPagerElementWidth
    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {
        scrollState = state
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            syncToViewPager.setCurrentItem(syncWithViewPager.currentItem, true)
        }
    }
}