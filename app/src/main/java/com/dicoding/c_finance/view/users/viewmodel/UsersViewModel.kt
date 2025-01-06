package com.dicoding.c_finance.view.users.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.c_finance.model.repo.FinanceRepository
import com.dicoding.c_finance.model.response.user.UsersItem
import kotlinx.coroutines.launch

class UsersViewModel(private val financeRepository: FinanceRepository) : ViewModel() {
    private val _userData = MutableLiveData<List<UsersItem>?>()
    val userData: LiveData<List<UsersItem>?> get() = _userData
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = financeRepository.getUsers()
            result.onSuccess { data ->
                _userData.value = data
                _isLoading.value = false
            }.onFailure { exception ->
                Log.e("UsersViewModel", "Error fetching data: $exception")
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = financeRepository.deleteUser(userId)
            result.onSuccess {
                fetchUsers()
                _isLoading.value = false
            }.onFailure { exception ->
                Log.e("UsersViewModel", "Error deleting user: $exception")
                _isLoading.value = false
            }
        }
    }
}