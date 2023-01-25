package com.ssafy.popcon.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.repository.user.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun signInNaver(user: User) {
        viewModelScope.launch {
            val user = userRepository.signInNaver(user)
            _user.value = user
        }
    }

    fun signInKakao(user: User) {
        viewModelScope.launch {
            val user = userRepository.signInKakao(user)
            _user.value = user
        }
    }

    fun withdraw(user: User) {
        viewModelScope.launch {
            val user = userRepository.withdraw(user)
            _user.value = user
        }
    }


}