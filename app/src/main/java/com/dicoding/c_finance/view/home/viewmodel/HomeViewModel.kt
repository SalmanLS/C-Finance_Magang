package com.dicoding.c_finance.view.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.c_finance.model.repo.FinanceRepository
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import kotlinx.coroutines.launch

class HomeViewModel(private val financeRepository: FinanceRepository): ViewModel() {
    private val _cashflowDataRecent = MutableLiveData<List<TransaksiItem>>()
    val cashflowDataRecent : LiveData<List<TransaksiItem>> get() = _cashflowDataRecent
    private val _cashflowData = MutableLiveData<List<TransaksiItem>>()
    val cashflowData : LiveData<List<TransaksiItem>> get() = _cashflowData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchCashflow() {
        viewModelScope.launch {
            _isLoading.value = true
            financeRepository.getCashflows().let { result ->
                result.onSuccess { data ->
                    _cashflowData.value = data
                    _isLoading.value = false
                }.onFailure { exception ->
                    _isLoading.value = false
                }
            }
        }
    }

    fun fetchRecentCashflow(){
        viewModelScope.launch {
            _isLoading.value = true
            financeRepository.getRecentCashflows().let { result ->
                result.onSuccess { data ->
                    _cashflowDataRecent.value = data
                    _isLoading.value = false
                }.onFailure { exception ->
                    _isLoading.value = false
                }
            }
        }
    }
}