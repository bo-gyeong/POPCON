package com.ssafy.popcon.viewmodel

import android.util.EventLog
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.repository.gifticon.GifticonRepository
import com.ssafy.popcon.ui.common.Event
import kotlinx.coroutines.launch

class PopupViewModel(private val gifticonRepository: GifticonRepository) : ViewModel() {
    val TAG = "POPUPVIEWMODEL"

    private val _brands = MutableLiveData<List<Brand>>()
    val brands: LiveData<List<Brand>> = _brands

    private val _gifticons = MutableLiveData<Event<List<Gifticon>>>()
    val gifticons: LiveData<Event<List<Gifticon>>> = _gifticons

    //현재위치에서 브랜드 받기
    fun getBrandByLocation(request: StoreRequest, user: User) {
        viewModelScope.launch {
            val brands = gifticonRepository.getBrandsByLocation(request)
            _brands.value = brands
            if (brands.size != 0) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          getGifticons(user, brands[0].brandName)
            }
        }
    }

    //상단 탭 클릭 시 브랜드 별 기프티콘 받음
    fun getGifticons(user: User, brandName: String) {
        viewModelScope.launch {
            val gifticons = gifticonRepository.getGifticonByBrand(
                GifticonByBrandRequest(
                    user.email!!,
                    user.social.toString(),
                    -1,
                    brandName
                )
            )
            Log.d(TAG, "getGifticons: $gifticons")
            _gifticons.value = Event(gifticons)
        }
    }

    fun updateGifticon(gifticon: UpdateRequest, user: User) {
        viewModelScope.launch {
            gifticonRepository.updateGifticon(gifticon)
            getGifticons(user, gifticon.brandName)
        }
    }
}