package com.dicoding.c_finance.view.cashflow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.c_finance.model.repo.FinanceRepository
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import com.dicoding.c_finance.utils.ExportState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CashflowViewModel(private val financeRepository: FinanceRepository): ViewModel() {
    private val _cashflowData = MutableLiveData<List<TransaksiItem>>()
    val cashflowData: LiveData<List<TransaksiItem>> get() = _cashflowData
    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> get() = _exportState
//    private val _fetchResult = MutableStateFlow<Result<GlobalResponse>?>(null)
//    val fetchResult: StateFlow<Result<GlobalResponse>?> = _fetchResult.asStateFlow()
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
    fun exportData(startDate: String, endDate: String) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            val result = financeRepository.exportData(startDate, endDate)
            result.fold(
                onSuccess = { responseBody ->
                    _exportState.value = ExportState.Success(responseBody)
                },
                onFailure = { error ->
                    _exportState.value = ExportState.Failure(error.message ?: "Unknown error")
                }
            )
        }
    }

}