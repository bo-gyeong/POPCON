package com.ssafy.popcon.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.repository.gifticon.GifticonRepository
import com.ssafy.popcon.repository.map.MapRepository
import kotlinx.coroutines.launch

class MapViewModel(
    private val gifticonRepository: GifticonRepository,
    private val mapRepository: MapRepository
) : ViewModel() {

    // 내가 가진 모든 기프티콘 저장하는 변수
    private val _mapGifticon = MutableLiveData<List<Gifticon>>()
    val mapGifticon: LiveData<List<Gifticon>> = _mapGifticon

    // 현재 위치 기반 지도에서 띄워줄 매장
    private var _store = MutableLiveData<List<Store>>()
    val store: LiveData<List<Store>> = _store

    private val _brandsMap = MutableLiveData<List<BrandResponse>>()
    val brandsMap: LiveData<List<BrandResponse>> = _brandsMap

    fun getStoreInfo(storeRequest: StoreRequest) {
        viewModelScope.launch {
            _store.value = mapRepository.getStoreByLocation(storeRequest)
        }
    }

    fun getStoreByBrand(storeByBrandRequest: StoreByBrandRequest) {
        viewModelScope.launch {
            _store.value = mapRepository.getStoreByBrand(storeByBrandRequest)
        }
    }

    fun getHomeBrand(user: User) {
        viewModelScope.launch {
            var homeBrand = gifticonRepository.getHomeBrands(user)
            _brandsMap.value = homeBrand
        }
    }

    //상단 탭 클릭리스너
    fun getGifticons(user: User, brandName: String) {
        if (brandName == "전체") {
            getGifticonByUser(user)
        } else {
            viewModelScope.launch {
                val gifticons = gifticonRepository.getGifticonByBrand(
                    GifticonByBrandRequest(
                        user.email!!,
                        user.social,
                        -1,
                        brandName
                    )
                )
                _mapGifticon.value = gifticons
            }
        }
    }

    //사용자의 기프티콘 목록 불러오기
    fun getGifticonByUser(user: User) {
        viewModelScope.launch {
            val gifticons = gifticonRepository.getGifticonByUser(user)

            _mapGifticon.value = gifticons
        }
    }
}