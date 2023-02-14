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
    @RequiresApi(Build.VERSION_CODES.O)
    fun calDday(gifticon: Gifticon): Badge {
        //0:사용가능, 1:사용완료, 2:기간만료
        return when (gifticon.state) {
            1 -> {
                Badge("사용완료", "#d2d2d2")

            }
            2 -> {
                Badge("기간만료", "#d2d2d2")
            }
            else -> {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val due = gifticon.due.split(" ")[0].format(dateFormat)
                var now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                val dueTime = dateFormat.parse(due)?.time
                val nowTime = dateFormat.parse(now)?.time

                val dDay = (dueTime!! - nowTime!!) / (24 * 60 * 60 * 1000)

                return if (dDay.toInt() == 0) {
                    Badge("오늘까지", "#FFFF0000")
                } else {
                    var color = "#8ED2CD"
                    if (dDay <= 3) {
                        color = "#CF6655"
                    } else if (dDay <= 7) {
                        color = "#FF9797"
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
}
