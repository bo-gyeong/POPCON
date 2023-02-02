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

    // 현재 위치 기반 지도에서 띄워줄
    private var _mapBrandLogo = MutableLiveData<List<MapBrandLogo>>()
    val mapBrandLogo: LiveData<List<MapBrandLogo>> = _mapBrandLogo

    private val _brandsMap = MutableLiveData<List<BrandResponse>>()
    val brandsMap: LiveData<List<BrandResponse>> = _brandsMap

    fun getStoreInfo(storeRequest: BrandRequest) {
        viewModelScope.launch {
            _mapBrandLogo.value = mapRepository.getStoreByLocation(storeRequest)
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
            Log.d("TAG", "getGifticonsssssss1: ")

            getGifticonByUser(user)
        } else {
            Log.d("TAG", "getGifticonsssssss1: ")
            viewModelScope.launch {
                val gifticons = gifticonRepository.getGifticonByBrand(
                    GifticonByBrandRequest(
                        user.email!!,
                        user.social,
                        -1,
                        brandName
                    )
                )
                Log.d("TAG", "getGifticonsssssss2: $gifticons")
                _mapGifticon.value = gifticons
                Log.d("TAG", "getGifticonsssssss3: $gifticons")

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