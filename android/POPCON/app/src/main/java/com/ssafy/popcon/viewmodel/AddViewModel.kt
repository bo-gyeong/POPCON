package com.ssafy.popcon.viewmodel

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
    val brandChk: LiveData<Event<ChkValidation>> = _brandChk

    private val _barcodeChk = MutableLiveData<Event<ChkValidation>>()
    val barcodeChk: LiveData<Event<ChkValidation>> = _barcodeChk

    private val _addGifticonResult = MutableLiveData<Event<List<AddInfoNoImg>>>()
    val addGifticonResult: LiveData<Event<List<AddInfoNoImg>>> = _addGifticonResult

    private val _gcpOtherResult = MutableLiveData<Event<List<GCPResult>>>()
    val gcpOtherResult: LiveData<Event<List<GCPResult>>> = _gcpOtherResult

    private val _addImgInfoResult = MutableLiveData<Event<List<List<AddImgInfoResult>>>>()
    val addImgInfoResult: LiveData<Event<List<List<AddImgInfoResult>>>> = _addImgInfoResult

    fun addFileToGCP(files: Array<MultipartBody.Part>){
        viewModelScope.launch {
            _gcpResult.value = Event(addRepository.addFileToGCP(files))
        }
    }

    fun useOcr(fileName: Array<OCRSend>){
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
            _addGifticonResult.value = Event(addRepository.addGifticon(addInfo))
        }
    }

    fun addOtherFileToGCP(files: Array<MultipartBody.Part>){
        viewModelScope.launch {
            _gcpOtherResult.value = Event(addRepository.addFileToGCP(files))
        }
    }

    fun addImgInfo(imgInfo: Array<AddImgInfo>){
        viewModelScope.launch {
            _addImgInfoResult.value = Event(addRepository.addImgInfo(imgInfo))
        }
    }
}