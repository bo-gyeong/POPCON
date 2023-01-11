package com.ssafy.popcon.ui.common

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ssafy.popcon.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat

@BindingAdapter("price")
fun applyPriceFormat(view: TextView, price: Int) {
    val decimalFormat = DecimalFormat("#,###")
    view.text = view.context.getString(R.string.unit_currency, decimalFormat.format(price))
}

@BindingAdapter("date")
fun applyDateFormat(view: TextView, date: String) {
    view.text = view.context.getString(R.string.due_date, date)
}
