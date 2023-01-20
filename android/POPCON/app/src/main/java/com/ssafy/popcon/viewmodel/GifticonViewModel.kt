package com.ssafy.popcon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.repository.gifticon.GifticonRepository
import com.ssafy.popcon.ui.common.Event
import kotlinx.coroutines.launch

class GifticonViewModel(private val gifticonRepository: GifticonRepository) : ViewModel() {
    private val _gifticons = MutableLiveData<List<Gifticon>>()
    val gifticons: LiveData<List<Gifticon>> = _gifticons

    private val _gifticonByBrand = MutableLiveData<List<Gifticon>>()
    val gifticonByBrand: LiveData<List<Gifticon>> = _gifticonByBrand

    private val _history = MutableLiveData<List<Gifticon>>()
    val history: LiveData<List<Gifticon>> = _history

    private val _openHistoryEvent = MutableLiveData<Event<String>>()
    val openHistoryEvent: LiveData<Event<String>> = _openHistoryEvent

    fun openHistory(userId: String) {
        _openHistoryEvent.value = Event(userId)
    }

    //사용자의 기프티콘 목록 불러오기
    private fun getGifticonByUserId(userId: String) {
        viewModelScope.launch {
            val gifticons = gifticonRepository.getGifticonByUserId(userId)
            _gifticons.value = gifticons
        }
    }

    //상단 탭 클릭리스너
    fun tabClickListener(user: User, brandName: String) {
        if (brandName == "히스토리") {
            //히스토리
            openHistory(user.email!!)

        } else {
            viewModelScope.launch {
                val gifticons = gifticonRepository.getGifticonByBrand(user.email!!, brandName)
                _gifticonByBrand.value = gifticons
            }
        }
    }

    //히스토리 목록 불러오기
    fun getHistory(userId: String) {
        viewModelScope.launch {
            val history = gifticonRepository.getHistory(userId)
            _history.value = history
        }
    }
}