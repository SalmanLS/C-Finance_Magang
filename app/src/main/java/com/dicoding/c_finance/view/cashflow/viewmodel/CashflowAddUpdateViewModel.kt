package com.dicoding.c_finance.view.cashflow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.c_finance.model.repo.FinanceRepository
import com.dicoding.c_finance.model.response.GlobalResponse
import com.dicoding.c_finance.model.response.category.CategoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CashflowAddUpdateViewModel(private val financeRepository: FinanceRepository) : ViewModel() {
    private val _categoryData = MutableLiveData<List<CategoryItem>?>()
    val categoryData: LiveData<List<CategoryItem>?> get() = _categoryData
    private val _addResult = MutableStateFlow<Result<GlobalResponse>?>(null)
    val addResult: StateFlow<Result<GlobalResponse>?> = _addResult.asStateFlow()
    private val _updateResult = MutableStateFlow<Result<GlobalResponse>?>(null)
    val updateResult: StateFlow<Result<GlobalResponse>?> = _updateResult.asStateFlow()
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchCategoryByType(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            financeRepository.getCategoryByType(id).let { result ->
                result.onSuccess { data ->
                    _categoryData.value = data
                    _isLoading.value = false
                }.onFailure { exception ->
                    _isLoading.value = false
                }
            }
        }
    }

    fun addCashflow(
        id_tipe: Int,
        id_kategori: Int,
        nominal: Int,
        tanggal: String,
        keterangan: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = financeRepository.addCashFlow(
                    id_tipe,
                    id_kategori,
                    nominal,
                    tanggal,
                    keterangan
                )
                _addResult.value = result
            } catch (e: Exception) {
                _addResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCashflow(
        id: Int,
        id_tipe: Int,
        id_kategori: Int,
        nominal: Int,
        tanggal: String,
        keterangan: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = financeRepository.updateCashflow(
                    id,
                    id_tipe,
                    id_kategori,
                    nominal,
                    keterangan,
                    tanggal
                )
                _updateResult.value = result
            } catch (e: Exception) {
                _updateResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}