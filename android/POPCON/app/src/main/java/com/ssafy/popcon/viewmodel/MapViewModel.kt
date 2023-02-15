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

    //상단 브랜드 리스트
    private val _brandsMap = MutableLiveData<List<BrandResponse>>()
    val brandsMap: LiveData<List<BrandResponse>> = _brandsMap

    private val _present = MutableLiveData<GifticonResponse>()
    val present: LiveData<GifticonResponse> = _present

    //현재 선택된 탭
    var brandName: String = "전체"

    //주변 선물 리스트
    private val _presents = MutableLiveData<List<Present>>()
    val presents: LiveData<List<Present>> = _presents

    //주울 수 있는 선물 리스트

    private val _presentsNear = MutableLiveData<List<Present>>()
    val presentsNear: LiveData<List<Present>> = _presentsNear

    fun getStoreInfo(storeRequest: StoreRequest) {
        viewModelScope.launch {
            _store.value = mapRepository.getStoreByLocation(storeRequest)
            brandName = "전체"
        }
    }

    fun getStoreByBrand(storeByBrandRequest: StoreByBrandRequest) {
        viewModelScope.launch {
            Log.d("TAG", "getStoreByBrand: $brandName")
            _store.value = mapRepository.getStoreByBrand(storeByBrandRequest)
            brandName = storeByBrandRequest.brandName
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
            val gifticons = gifticonRepository.getGifticonMapByUser(user)

            _mapGifticon.value = gifticons
        }
    }

    fun getAllPresents(findPresentRequest: FindPresentRequest) {
        viewModelScope.launch {
            val presents = mapRepository.getPresents(findPresentRequest)
            _presents.value = presents.allNearPresentList
            _presentsNear.value = presents.gettablePresentList
        }
    }

    fun donate(donateRequest: DonateRequest, user: User, x: String, y: String) {
        viewModelScope.launch {
            mapRepository.donate(donateRequest)
            getAllPresents(FindPresentRequest(x, y))
            getGifticons(user, brandName)
            getHomeBrand(user)
        }
    }

    //줍기
    fun getPresent(getPresentRequest: GetPresentRequest, user: User) {
        viewModelScope.launch {
            mapRepository.getPresent(getPresentRequest)
            getAllPresents(FindPresentRequest(getPresentRequest.x, getPresentRequest.y))
            getGifticons(user, brandName)
            getHomeBrand(user)
        }
    }

    fun getGifticonByBarcodeNum(barcodeNum: String) {
        viewModelScope.launch {
            _present.value = gifticonRepository.getGifticonByBarNum(barcodeNum)
        }
    }
}