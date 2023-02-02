package com.ssafy.popcon.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditViewModel() : ViewModel() {
    private val _barNum = MutableLiveData<String>()
    val barNum: LiveData<String> = _barNum

    fun setBarNum(barNum: String) {
        _barNum.value = barNum
    }
}