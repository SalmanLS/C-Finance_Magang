package com.dicoding.c_finance.view.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dicoding.c_finance.model.pref.UserToken
import com.dicoding.c_finance.model.repo.FinanceRepository

class MainViewModel(private val financeRepository: FinanceRepository) : ViewModel() {
    fun getSession(): LiveData<UserToken?> = liveData {
        val token = financeRepository.getToken()
        emit(token)
    }
}