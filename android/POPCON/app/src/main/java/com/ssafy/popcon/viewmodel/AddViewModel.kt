package com.ssafy.popcon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.AddInfo
import com.ssafy.popcon.dto.AddInfoNoImg
import com.ssafy.popcon.repository.add.AddRepository
import kotlinx.coroutines.launch

class AddViewModel(private val addRepository: AddRepository): ViewModel() {
    fun useOcr(filePath: String){
        viewModelScope.launch {
            addRepository.useOcr(filePath)
        }
    }

    fun addGifticon(addInfo: List<AddInfoNoImg>){
        viewModelScope.launch {
            addRepository.addGifticon(addInfo)
        }
    }
}