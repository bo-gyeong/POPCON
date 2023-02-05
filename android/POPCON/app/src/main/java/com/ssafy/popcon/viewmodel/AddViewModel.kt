package com.ssafy.popcon.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.repository.add.AddRepository
import com.ssafy.popcon.ui.common.Event
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AddViewModel(private val addRepository: AddRepository): ViewModel() {
    private val _gcpResult = MutableLiveData<Event<List<GCPResult>>>()
    val gcpResult: LiveData<Event<List<GCPResult>>> = _gcpResult

    private val _ocrResult = MutableLiveData<Event<List<OCRResult>>>()
    val ocrResult: LiveData<Event<List<OCRResult>>> = _ocrResult

    private val _brandChk = MutableLiveData<Event<ChkValidation>>()
    val brandChk = _brandChk

    private val _barcodeChk = MutableLiveData<Event<ChkValidation>>()
    val barcodeChk = _barcodeChk

    fun addFileToGCP(files: Array<MultipartBody.Part>){
        viewModelScope.launch {
            _gcpResult.postValue(Event(addRepository.addFileToGCP(files)))
        }
    }

    fun useOcr(fileName: Array<String>){
        viewModelScope.launch {
            _ocrResult.value = Event(addRepository.useOcr(fileName))
        }
    }

    fun chkBrand(brandName: String){
        viewModelScope.launch {
            _brandChk.value = Event(addRepository.chkBrand(brandName))
        }
    }

    fun chkBarcode(barcodeNum: String){
        viewModelScope.launch {
            _barcodeChk.value = Event(addRepository.chkBarcode(barcodeNum))
        }
    }

    fun addGifticon(addInfo: List<AddInfoNoImg>){
        viewModelScope.launch {
            addRepository.addGifticon(addInfo)
        }
    }

    fun addGifticonImg(
        files:Array<MultipartBody.Part>,
        imgInfo: Array<AddImgInfo>
    ){
        viewModelScope.launch {
            addRepository.addGifticonImg(files, imgInfo)
        }
    }
}