package com.ssafy.popcon.ui.common

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.ssafy.popcon.databinding.SnackbarPopconBinding

class PopconSnackBar(view: View, private val message: String) {

    companion object {
        fun make(view: View, message: String) = PopconSnackBar(view, message)
    }

    private val context = view.context
    private val snackbar = Snackbar.make(view, "", 1500)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val snackbarBinding = SnackbarPopconBinding.inflate(inflater, null, false)

    init {
        initView()
        initData()
    }

    private fun initView() {
        with(snackbarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            addView(snackbarBinding.root, 0)
        }
    }

    private fun initData() {
        snackbarBinding.tvSnackbar.text = message
    }

    fun show() {
        snackbar.show()
    }
}