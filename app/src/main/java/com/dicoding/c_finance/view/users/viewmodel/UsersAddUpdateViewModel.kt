package com.dicoding.c_finance.view.users.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.c_finance.model.repo.FinanceRepository
import com.dicoding.c_finance.model.response.user.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsersAddUpdateViewModel(private val financeRepository: FinanceRepository) : ViewModel() {
    private val _addResult = MutableStateFlow<Result<UserResponse>?>(null)
    val addResult: StateFlow<Result<UserResponse>?> = _addResult.asStateFlow()
    private val _updateResult = MutableStateFlow<Result<UserResponse>?>(null)
    val updateResult: StateFlow<Result<UserResponse>?> = _updateResult.asStateFlow()
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun addUser(username: String, password: String, phone: String, id_role: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = financeRepository.addUser(username, password, phone, id_role)
                _addResult.value = result
            } catch (e: Exception) {
                _addResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser(id: Int, username: String, password: String, phone: String, id_role: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = financeRepository.updateUser(id, username, password, phone, id_role)
                _updateResult.value = result
            } catch (e: Exception) {
                _updateResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}