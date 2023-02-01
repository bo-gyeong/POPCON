package com.ssafy.popcon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.AddInfo
import com.ssafy.popcon.dto.AddInfoNoImg
import com.ssafy.popcon.dto.gcpResult
import com.ssafy.popcon.repository.add.AddRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AddViewModel(private val addRepository: AddRepository): ViewModel() {
    private val _gcpResult = MutableLiveData<List<gcpResult>>()
    val gcpResult: LiveData<List<gcpResult>> = _gcpResult

    fun addFileToGCP(files: Array<MultipartBody.Part>){
        viewModelScope.launch {
            _gcpResult.value = addRepository.addFileToGCP(files)
        }
    }

    fun useOcr(fileName: String){
        viewModelScope.launch {
            addRepository.useOcr(fileName)
        }
    }

    fun addGifticon(addInfo: List<AddInfoNoImg>){
        viewModelScope.launch {
            addRepository.addGifticon(addInfo)
        }
    }

    fun addGifticonImg(files: List<MultipartBody.Part>){
        viewModelScope.launch {
            addRepository.addGifticonImg(files)
        }
    }
}