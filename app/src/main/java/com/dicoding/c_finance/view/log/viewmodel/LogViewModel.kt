package com.dicoding.c_finance.view.log.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.c_finance.model.repo.FinanceRepository
import com.dicoding.c_finance.model.response.GlobalResponse
import com.dicoding.c_finance.model.response.log.LogItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogViewModel(private val financeRepository: FinanceRepository) : ViewModel() {
    private val _logResult = MutableStateFlow<Result<GlobalResponse>?>(null)
    val logResult: StateFlow<Result<GlobalResponse>?> = _logResult.asStateFlow()
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    val pagingLog: LiveData<PagingData<LogItem>> =
        financeRepository.getLogs().cachedIn(viewModelScope)

    fun deleteLog(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = financeRepository.deleteLogs(id)
                _logResult.value = result
            } catch (e: Exception) {
                _logResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
                _logResult.value = null
            }
        }
    }
}