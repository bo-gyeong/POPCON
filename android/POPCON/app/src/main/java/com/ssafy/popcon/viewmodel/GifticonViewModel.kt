package com.ssafy.popcon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.repository.gifticon.GifticonRepository
import com.ssafy.popcon.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

class GifticonViewModel(private val gifticonRepository: GifticonRepository) : ViewModel() {
    private val _gifticons = MutableLiveData<List<Gifticon>>()
    val gifticons: LiveData<List<Gifticon>> = _gifticons

    private val _gifticonByBrand = MutableLiveData<List<Gifticon>>()
    val gifticonByBrand: LiveData<List<Gifticon>> = _gifticonByBrand

    private val _history = MutableLiveData<List<Gifticon>>()
    val history: LiveData<List<Gifticon>> = _history

    private fun getGifticonByUserId(userId: String) {
        viewModelScope.launch {
            val gifticons = gifticonRepository.getGifticonByUserId(userId)
            _gifticons.value = gifticons
        }
    }

    private fun getGifticonByBrand(userId: String, brandName: String) {
        if (brandName == "히스토리") {
            //히스토리
            getHistory(userId)
        } else {
            viewModelScope.launch {
                val gifticons = gifticonRepository.getGifticonByBrand(userId, brandName)
                _gifticonByBrand.value = gifticons
            }
        }
    }

    private fun getHistory(userId: String) {
        viewModelScope.launch {
            val history = gifticonRepository.getHistory(userId)
            _history.value = history
        }
    }
}