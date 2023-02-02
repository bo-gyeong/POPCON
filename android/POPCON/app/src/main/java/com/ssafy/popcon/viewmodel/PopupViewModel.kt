package com.ssafy.popcon.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.repository.add.AddRepository
import com.ssafy.popcon.repository.gifticon.GifticonRepository
import kotlinx.coroutines.launch

class PopupViewModel(private val gifticonRepository: GifticonRepository): ViewModel() {
    private val _brands = MutableLiveData<List<Brand>>()
    val brands: LiveData<List<Brand>> = _brands

    private val _gifticons = MutableLiveData<List<Gifticon>>()
    val gifticons: LiveData<List<Gifticon>> = _gifticons

    //현재위치에서 브랜드 받기
    fun getBrandByLocation(request: BrandRequest) {
        viewModelScope.launch {
            val brands = gifticonRepository.getBrandsByLocation(request)
            _brands.value = brands
        }
    }

    //상단 탭 클릭리스너
    fun getGifticons(user: User, brandName: String) {
        viewModelScope.launch {
            val gifticons = gifticonRepository.getGifticonByBrand(GifticonByBrandRequest(user.email!!, user.social.toString(), -1, brandName))
            _gifticons.value = gifticons
        }
    }
}