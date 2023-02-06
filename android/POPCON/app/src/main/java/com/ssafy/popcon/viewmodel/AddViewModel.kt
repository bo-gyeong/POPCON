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

private const val TAG = "AddViewModel___"
class AddViewModel(private val addRepository: AddRepository): ViewModel() {
    private val _gcpResult = MutableLiveData<Event<List<GCPResult>>>()
    val gcpResult: LiveData<Event<List<GCPResult>>> = _gcpResult

    private val _ocrResult = MutableLiveData<Event<List<OCRResult>>>()
    val ocrResult: LiveData<Event<List<OCRResult>>> = _ocrResult

    private val _brandChk = MutableLiveData<Event<ChkValidation>>()
    val brandChk = _brandChk

    private val _barcodeChk = MutableLiveData<Event<ChkValidation>>()
    val barcodeChk = _barcodeChk

    private val _addImgInfoResult = MutableLiveData<Event<List<AddImgInfoResult>>>()
    val addImgInfoResult = _addImgInfoResult

    fun addFileToGCP(files: Array<MultipartBody.Part>){
        viewModelScope.launch {
            Log.d(TAG, "addFileToGCP: 111")
            _gcpResult.value = Event(addRepository.addFileToGCP(files))
            //addImgInfo(makeAddImgInfoList())
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
            Log.d(TAG, "addGifticon: 222")
            addRepository.addGifticon(addInfo)
        }
    }

    fun addImgInfo(imgInfo: Array<AddImgInfo>){
        Log.d(TAG, "addImgInfo: 111")
        viewModelScope.launch {
            Log.d(TAG, "addImgInfo: 222")
            addImgInfoResult.value = Event(addRepository.addImgInfo(imgInfo))
        }
    }
}