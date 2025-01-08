package com.dicoding.c_finance.view.category.viewmodel

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

class CategoryViewModel(private val financeRepository: FinanceRepository) : ViewModel() {

    private val _selectedType = MutableLiveData<Int>()
    val selectedType: LiveData<Int> get() = _selectedType
    private val _categoryResult = MutableStateFlow<Result<GlobalResponse>?>(null)
    val categoryResult: StateFlow<Result<GlobalResponse>?> = _categoryResult.asStateFlow()
    private val _categoryData = MutableLiveData<List<CategoryItem>?>()
    val categoryData: LiveData<List<CategoryItem>?> get() = _categoryData
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isLoading2 = MutableLiveData(false)
    val isLoading2: LiveData<Boolean> = _isLoading2

    fun setSelectedType(type: Int) {
        _selectedType.value = type
    }

    fun fetchCategoryByType(idType: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            financeRepository.getCategoryByType(idType).let { result ->
                result.onSuccess { data ->
                    _categoryData.value = data
                }.onFailure {
                    _categoryData.value = emptyList()
                }
                _isLoading.value = false
            }
        }
    }

    fun addCategory(name: String, idType: Int) {
        viewModelScope.launch {
            try {
                _isLoading2.value = true
                val result = financeRepository.addCategory(idType, name)
                _categoryResult.value = result
            } catch (e: Exception) {
                _categoryResult.value = Result.failure(e)
            } finally {
                _isLoading2.value = false
                _categoryResult.value = null
            }
        }
    }

    fun updateCategory(id: Int, name: String, idType: Int) {
        viewModelScope.launch {
            try {
                _isLoading2.value = true
                val result = financeRepository.updateCategory(id, idType, name)
                _categoryResult.value = result
            } catch (e: Exception) {
                _categoryResult.value = Result.failure(e)
            } finally {
                _isLoading2.value = false
                _categoryResult.value = null
            }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading2.value = true
                val result = financeRepository.deleteCategory(id)
                _categoryResult.value = result
            } catch (e: Exception) {
                _categoryResult.value = Result.failure(e)
            } finally {
                _isLoading2.value = false
                _categoryResult.value = null
            }
        }
    }
}
