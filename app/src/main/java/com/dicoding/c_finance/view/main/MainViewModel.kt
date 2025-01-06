package com.dicoding.c_finance.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dicoding.c_finance.model.pref.UserToken
import com.dicoding.c_finance.model.repo.UserRepository

class MainViewModel(private val userRepository: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserToken?> = liveData {
        val token = userRepository.getToken()
        emit(token)
    }
}