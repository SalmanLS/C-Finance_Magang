package com.dicoding.c_finance.view.category.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.c_finance.model.repo.FinanceRepository
import com.dicoding.c_finance.model.response.category.CategoryItem
import kotlinx.coroutines.launch

class CategoryViewModel(private val financeRepository: FinanceRepository) : ViewModel() {
    private val _categoryData = MutableLiveData<List<CategoryItem>?>()
    val categoryData: LiveData<List<CategoryItem>?> get() = _categoryData
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

    fun addCategory(nama: String, id_type: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            financeRepository.addCategory(id_type, nama).let { result ->
                result.onSuccess {
                    fetchCategoryByType(id_type)
                    _isLoading.value = false
                }.onFailure { exception ->
                    _isLoading.value = false
                }
            }
        }
    }

    fun updateCategory(id: Int, nama: String, id_type: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            financeRepository.updateCategory(id, id_type, nama).let { result ->
                result.onSuccess {
                    fetchCategoryByType(id_type)
                    _isLoading.value = false
                }.onFailure { exception ->
                    _isLoading.value = false
                }
            }
        }
    }

    fun deleteCategory(id_kategori: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            financeRepository.deleteCategory(id_kategori).let { result ->
                result.onSuccess {
                    _isLoading.value = false
                }.onFailure { exception ->
                    _isLoading.value = false
                }
            }
        }
    }
}