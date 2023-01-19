package com.ssafy.popcon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.repository.user.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun naverSignIn(user: User) {
        viewModelScope.launch {
            userRepository.signIn(user)
        }
    }
}