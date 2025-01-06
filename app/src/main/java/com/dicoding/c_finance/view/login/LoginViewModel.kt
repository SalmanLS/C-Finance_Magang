package com.dicoding.c_finance.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.c_finance.model.repo.UserRepository
import com.dicoding.c_finance.model.response.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository): ViewModel(){
    private val _loginResult = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginResult: StateFlow<Result<LoginResponse>?> = _loginResult.asStateFlow()
    private val _isLoading = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading

    fun login(username: String, password: String){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = userRepository.login(username, password)
                _loginResult.value = result
                _isLoading.value = false
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }
}