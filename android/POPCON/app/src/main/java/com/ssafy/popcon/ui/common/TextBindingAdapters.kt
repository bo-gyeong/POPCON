package com.ssafy.popcon.ui.common

import android.util.Log
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.ssafy.popcon.R
import java.text.DecimalFormat

//가격에 원, 콤마
@BindingAdapter("price")
fun applyPriceFormat(view: TextView, price: Int) {
    val decimalFormat = DecimalFormat("#,###")
    view.text = view.context.getString(R.string.unit_currency, decimalFormat.format(price))
}

//날짜에 까지 넣기
@BindingAdapter("date")
fun applyDateFormat(view: TextView, date: String?) {
    if (date.isNullOrEmpty()) {
        view.isVisible = false
    } else {
        val due = date.split(" ")[0]
        view.text = view.context.getString(R.string.due_date, due)
    }
}
