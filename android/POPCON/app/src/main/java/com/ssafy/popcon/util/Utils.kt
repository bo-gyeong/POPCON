package com.ssafy.popcon.util

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.streams.toList

object Utils {
    //상태바 투명처리
    fun Activity.setStatusBarTransparent() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if (Build.VERSION.SDK_INT >= 30) {    // API 30 에 적용
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
    fun calDday(gifticon: Gifticon): Badge {
        //0:사용가능, 1:사용완료, 2:기간만료
        return when (gifticon.state) {
            1 -> {
                Badge("사용완료", "#5f5f69")

            }
            2 -> {
                Badge("기간만료", "#5f5f69")
            }
            else -> {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val due = gifticon.due.split(" ")[0].format(dateFormat)
                var now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                val dueTime = dateFormat.parse(due)?.time
                val nowTime = dateFormat.parse(now)?.time

                val dDay = (dueTime!! - nowTime!!) / (24 * 60 * 60 * 1000)

                return if (dDay.toInt() == 0) {
                    Badge("오늘까지", "#FF5755")
                } else if (dDay.toInt() < 0) {
                    Badge("기간만료", "#5f5f69")
                } else {
                    var color = "#8ED2CD"
                    if (dDay <= 3) {
                        color = "#FF9796"
                    } else if (dDay <= 7) {
                        color = "#F7B733"
                    }

                    Badge("D-$dDay", color)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calState(gifticon: Gifticon): Int {
        //0:사용가능, 1:사용완료, 2:기간만료

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val due = gifticon.due.split(" ")[0].format(dateFormat)
        var now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val dueTime = dateFormat.parse(due)?.time
        val nowTime = dateFormat.parse(now)?.time

        val dDay = (dueTime!! - nowTime!!) / (24 * 60 * 60 * 1000)

        return if (dDay.toInt() >= 0) {
            return 0
        } else {
            return 2
        }
    }


    fun makeDateTimeException(eventDate: String): String {
//    eventDate : 2023-07-14 10:12:14
        val cal = Calendar.getInstance()
        var t_dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        val date: Date = t_dateFormat.parse(eventDate)
        cal.time = date
        return "${t_dateFormat.format(cal.time)}"
    }

    fun findRemainingDay(eventDate: String): Int {
//    eventDate : 2023-07-14 10:12:14
        var today = Calendar.getInstance()
        var sf = SimpleDateFormat("yyyy-MM-dd 00:00:00")
        var eventDate = sf.parse(eventDate)
        val remainingDay = (eventDate.time - today.time.time) / (60 * 60 * 24 * 1000)
        return remainingDay.toInt() + 1
    }
}
