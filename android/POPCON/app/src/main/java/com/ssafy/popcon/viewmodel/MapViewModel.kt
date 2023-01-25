package com.ssafy.popcon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.config.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.MapBrandLogo
import com.ssafy.popcon.dto.MapNowPos
import com.ssafy.popcon.repository.map.MapRepository
import com.ssafy.popcon.ui.map.MapFragment
import kotlinx.coroutines.launch

class MapViewModel(private val mapRepository: MapRepository) : ViewModel() {

    // 내가 가진 모든 기프티콘 저장하는 변수
    private val _mapGifticon = MutableLiveData<List<Gifticon>>()
    val mapGifticon: LiveData<List<Gifticon>> = _mapGifticon

    // 현재 위치 기반 지도에서 띄워줄
    private var _mapBrandLogo = MutableLiveData<List<MapBrandLogo>>()
    val mapBrandLogo: LiveData<List<MapBrandLogo>> = _mapBrandLogo

    fun sendUserPosition(nowPos: Map<String, String>) {
        viewModelScope.launch {
            _mapBrandLogo.value = mapRepository.sendUserPosition(nowPos)
        }
    }
//    fun sendUserPosition(mapNowPos: MapNowPos) {
//        viewModelScope.launch {
//            _mapBrandLogo.value = mapRepository.sendUserPosition(mapNowPos)
//        }
//    }
//    fun sendUserPosition(email: String, social: Int, x: String, y: String, radius: String) {
//        viewModelScope.launch {
//            _mapBrandLogo.value = mapRepository.sendUserPosition(email, social, x, y, radius
//            )
//        }
//    }
}