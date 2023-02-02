package com.ssafy.popcon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.repository.add.AddRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AddViewModel(private val addRepository: AddRepository): ViewModel() {
    private val _gcpResult = MutableLiveData<List<GCPResult>>()
    val gcpResult: LiveData<List<GCPResult>> = _gcpResult

    private val _ocrResult = MutableLiveData<List<OCRResult>>()
    val ocrResult: LiveData<List<OCRResult>> = _ocrResult

    fun addFileToGCP(files: Array<MultipartBody.Part>){
        viewModelScope.launch {
            _gcpResult.value = addRepository.addFileToGCP(files)
        }
    }

    fun useOcr(fileName: Array<String>){
        viewModelScope.launch {
            _ocrResult.value = addRepository.useOcr(fileName)
        }
    }

    fun addGifticon(addInfo: List<AddInfoNoImg>){
        viewModelScope.launch {
            addRepository.addGifticon(addInfo)
        }
    }

    fun addGifticonImg(imgInfo: Array<AddImgInfo>){
        viewModelScope.launch {
            addRepository.addGifticonImg(imgInfo)
        }
    }
}