package com.ssafy.popcon.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.DonateRequest
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.repository.user.WearRepository
import kotlinx.coroutines.launch

class WearViewModel(private val wearRepository: WearRepository) : ViewModel() {
    val TAG = "WEAR VIEWMODEL"
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _gifticons = MutableLiveData<List<Gifticon>>()
    val gifticons: LiveData<List<Gifticon>> = _gifticons

    //사용자의 기프티콘 목록 불러오기
    fun getGifticonByUser(user: User) {
        Log.d(TAG, "getGifticonByUser: $user")
        viewModelScope.launch {
            val gifticons = wearRepository.getGifticonByUser(user)
            _gifticons.value = gifticons
        }
    }

    fun donate(donateRequest: DonateRequest, user: User) {
        viewModelScope.launch {
            wearRepository.donate(donateRequest)
            getGifticonByUser(user)
        }
    }
}