package com.ssafy.popcon.util

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Gifticon
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {
    //상태바 투명처리
    fun Activity.setStatusBarTransparent() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if(Build.VERSION.SDK_INT >= 30) {	// API 30 에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    //하단바 높이 구하기
    fun Context.navigationHeight(): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")

        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calDday(gifticon: Gifticon) : Badge{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val due = gifticon.due.split(" ")[0].format(dateFormat)
        var now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val dueTime = dateFormat.parse(due)?.time
        val nowTime = dateFormat.parse(now)?.time

        val dDay = (dueTime!! - nowTime!!) / (24 * 60 * 60 * 1000)
        var color = "#8ED2CD"
        if (dDay <= 3) {
            color = "#CF6655"
        }else if (dDay <= 7) {
            color = "#FF9797"
        }

        return Badge("D-$dDay", color)
    }
}