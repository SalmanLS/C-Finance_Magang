package com.dicoding.c_finance.utils

import android.content.Context
import com.dicoding.c_finance.model.api.ApiConfig
import com.dicoding.c_finance.model.pref.UserPreference
import com.dicoding.c_finance.model.repo.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context)
        val user = runBlocking {
            pref.getToken().first()
        }
        val apiService = ApiConfig.getApiService(user.token)
        val repository = UserRepository.getInstance(pref, apiService)
        return repository
    }
}