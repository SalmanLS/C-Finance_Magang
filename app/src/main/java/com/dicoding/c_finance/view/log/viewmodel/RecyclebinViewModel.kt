package com.dicoding.c_finance.view.log.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.c_finance.model.repo.FinanceRepository
import com.dicoding.c_finance.model.response.recyclebin.RecycleBinItem
import kotlinx.coroutines.launch

class RecyclebinViewModel(private val financeRepository: FinanceRepository): ViewModel() {
    private val _recycleData = MutableLiveData<List<RecycleBinItem>?>()
    val recycleData: LiveData<List<RecycleBinItem>?> get() = _recycleData
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchRecyclebin() {
        viewModelScope.launch {
            _isLoading.value = true
            financeRepository.getRecycleBin().let { result ->
                result.onSuccess { data ->
                    _recycleData.value = data
                    _isLoading.value = false
                }.onFailure { exception ->
                    _isLoading.value = false
                }
            }
        }
    }

    fun deleteRecyclebin(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            financeRepository.deleteRecycleBin(id).let { result ->
                result.onSuccess {
                    fetchRecyclebin()
                    _isLoading.value = false
                }.onFailure { exception ->
                    _isLoading.value = false
                }
            }
        }
    }
}