package com.ssafy.popcon.viewmodel

import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.repository.gifticon.GifticonRepository
import com.ssafy.popcon.ui.common.Event
import com.ssafy.popcon.ui.common.PopconSnackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.streams.toList

class GifticonViewModel(private val gifticonRepository: GifticonRepository) : ViewModel() {
    private val _gifticons = MutableLiveData<List<Gifticon>>()
    val gifticons: LiveData<List<Gifticon>> = _gifticons

    private val _history = MutableLiveData<List<Gifticon>>()
    val history: LiveData<List<Gifticon>> = _history

    private val _brandsHome = MutableLiveData<List<BrandResponse>>()
    val brandsHome: LiveData<List<BrandResponse>> = _brandsHome

    private val _gifticon = MutableLiveData<GifticonResponse>()
    val gifticon: LiveData<GifticonResponse> = _gifticon

    private val _openGifticonDialogEvent = MutableLiveData<Event<Gifticon>>()
    val openGifticonDialogEvent = _openGifticonDialogEvent

    /*private val _historyItem = MutableLiveData<GifticonResponse>()
    val historyItem: LiveData<GifticonResponse> = _historyItem*/

    fun getGifticonByBarcodeNum(barcodeNum: String) {
        viewModelScope.launch {
            val gifticon = gifticonRepository.getGifticonByBarNum(barcodeNum)
            _gifticon.value = gifticon
        }
    }

    fun openGifticonDialog(gifticon: Gifticon) {
        _openGifticonDialogEvent.value = Event(gifticon)
        getGifticonByBarcodeNum(gifticon.barcodeNum)
    }

    //사용자의 기프티콘 목록 불러오기
    fun getGifticonByUser(user: User) {
        viewModelScope.launch {
            val gifticons = gifticonRepository.getGifticonByUser(user)
            _gifticons.value = gifticons
        }
    }

    fun getHomeBrand(user: User) {
        viewModelScope.launch {
            var homeBrand = gifticonRepository.getHomeBrands(user)
            _brandsHome.value = homeBrand
        }
    }

    fun deleteGifticon(barcodeNum: DeleteRequest, user: User) {
        viewModelScope.launch {
            gifticonRepository.deleteGifticon(barcodeNum)
            getGifticonByUser(user)
            getHomeBrand(user)
        }
    }

    //상단 탭 클릭리스너
    fun getGifticons(user: User, brandName: String) {
        if (brandName == "전체") {
            getGifticonByUser(user)
        } else {
            viewModelScope.launch {
                Log.d("TAG", "getGifticons: $brandName")
                val gifticons = gifticonRepository.getGifticonByBrand(
                    GifticonByBrandRequest(
                        user.email!!,
                        user.social.toString(),
                        -1,
                        brandName
                    )
                )
                Log.d("TAG", "getGifticonssssss: $gifticons")
                _gifticons.value = gifticons
            }
        }
    }

    //히스토리 목록 불러오기
    fun getHistory(req: UserDeleteRequest) {
        viewModelScope.launch {
            val history = gifticonRepository.getHistory(req)
            _history.value = history
        }
    }

    //기프티콘 업데이트
    fun updateGifticon(gifticon: UpdateRequest, user: User) {
        viewModelScope.launch {
            gifticonRepository.updateGifticon(gifticon)
            getHistory(UserDeleteRequest(user.email!!, user.social))
            getHomeBrand(user)
            getGifticonByUser(user)
        }
    }
}