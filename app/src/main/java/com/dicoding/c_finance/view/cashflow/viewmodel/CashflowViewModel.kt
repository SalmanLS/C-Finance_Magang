package com.dicoding.c_finance.view.cashflow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.c_finance.model.repo.FinanceRepository
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import kotlinx.coroutines.launch

class CashflowViewModel(private val financeRepository: FinanceRepository): ViewModel() {
    private val _cashflowData = MutableLiveData<List<TransaksiItem>?>()
    val cashflowData: LiveData<List<TransaksiItem>?> get() = _cashflowData
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

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

    fun deleteCashflow(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            financeRepository.deleteCashflow(id).let { result ->
                result.onSuccess {
                    fetchCashflow()
                    _isLoading.value = false
                }.onFailure { exception ->
                    _isLoading.value = false
                }
            }
        }
    }
}