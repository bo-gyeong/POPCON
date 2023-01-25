package com.ssafy.popcon.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.BrandRequest
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.repository.gifticon.GifticonRepository
import com.ssafy.popcon.ui.common.Event
import kotlinx.coroutines.launch
import kotlin.streams.toList

class GifticonViewModel(private val gifticonRepository: GifticonRepository) : ViewModel() {
    private val _gifticons = MutableLiveData<List<Gifticon>>()
    val gifticons: LiveData<List<Gifticon>> = _gifticons

    private val _gifticonByBrand = MutableLiveData<List<Gifticon>>()
    val gifticonByBrand: LiveData<List<Gifticon>> = _gifticonByBrand

    private val _history = MutableLiveData<List<Gifticon>>()
    val history: LiveData<List<Gifticon>> = _history

    private val _openHistoryEvent = MutableLiveData<Event<String>>()
    val openHistoryEvent: LiveData<Event<String>> = _openHistoryEvent

    private val _brands = MutableLiveData<List<Brand>>()
    val brands: LiveData<List<Brand>> = _brands

    fun openHistory(userId: String) {
        _openHistoryEvent.value = Event(userId)
    }

    //사용자의 기프티콘 목록 불러오기
    fun getGifticonByUser(user: User) {
        viewModelScope.launch {
            val gifticons = gifticonRepository.getGifticonByUser(user)

            _gifticons.value = gifticons
        }
    }

    //상단 탭 클릭리스너
    fun tabClickListener(user: User, brandName: String) {
        if (brandName == "히스토리") {
            //히스토리
            openHistory(user.email!!)
        } else if (brandName == "전체") {
            getGifticonByUser(user)
        } else {
            viewModelScope.launch {
                val gifticons = gifticonRepository.getGifticonByBrand(user.email!!, brandName)
                _gifticons.value = gifticons
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

    //기프티콘 업데이트
    fun updateGifticon(gifticon: Gifticon) {
        viewModelScope.launch {
            gifticonRepository.updateGifticon(gifticon)
        }
    }

    fun getBrandByLocation(request: BrandRequest) {
        viewModelScope.launch {
            val brands = gifticonRepository.getBrandsByLocation(request)
            _brands.value = brands
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getBrands(): List<Brand> {
        return gifticons.value?.stream()?.map { gc -> gc.brand }?.distinct()!!.toList()
    }
}