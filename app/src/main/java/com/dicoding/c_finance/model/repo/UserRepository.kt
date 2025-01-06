package com.dicoding.c_finance.model.repo

import android.util.Log
import com.dicoding.c_finance.model.api.ApiConfig
import com.dicoding.c_finance.model.api.ApiService
import com.dicoding.c_finance.model.pref.UserPreference
import com.dicoding.c_finance.model.pref.UserToken
import com.dicoding.c_finance.model.response.LoginResponse
import kotlinx.coroutines.flow.firstOrNull

class UserRepository private constructor(
    var apiService: ApiService,
    private val userPreference: UserPreference
){
    suspend fun login(username: String, password: String):Result<LoginResponse>{
        return try {
            val response = apiService.login(username, password)
            if (response.status == "error") {
                Result.failure(Exception("Login failed, Check your credentials"))
            } else {
                userPreference.saveToken(
                    UserToken(
                        response.username ?: "",
                        response.token ?: ""
                    )
                )
                apiService = ApiConfig.getApiService(response.token ?: "")
                Log.d("UserRepository", "token: ${response.token}")
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getToken(): UserToken? {
        return userPreference.getToken().firstOrNull()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}