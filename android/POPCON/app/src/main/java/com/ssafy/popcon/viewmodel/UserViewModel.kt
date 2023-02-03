package com.ssafy.popcon.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.SigninResponse
import com.ssafy.popcon.dto.UpdateUserInfo
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.dto.UserDeleteRequest
import com.ssafy.popcon.repository.user.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _tokens = MutableLiveData<SigninResponse>()
    val tokens : LiveData<SigninResponse> = _tokens

    fun signIn(user : User){
        viewModelScope.launch {
            Log.d("TAG", "signIn: $user")
            val response = userRepository.signIn(user)
            _tokens.value = response
        }
    }

    fun signInKakao(user: User) {
        viewModelScope.launch {
            val user = userRepository.signInKakao(user)
            _user.value = user
        }
    }

    fun withdraw(user: UserDeleteRequest) {
        viewModelScope.launch {
            userRepository.withdraw(user)
        }
    }

    fun updateUser(user: User){
        viewModelScope.launch {
            _user.value = userRepository.updateUser(user)
        }
    }
}