package com.dicoding.c_finance.model.repo

import android.util.Log
import com.dicoding.c_finance.model.api.ApiConfig
import com.dicoding.c_finance.model.api.ApiService
import com.dicoding.c_finance.model.pref.UserPreference
import com.dicoding.c_finance.model.pref.UserToken
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import com.dicoding.c_finance.model.response.category.CategoryItem
import com.dicoding.c_finance.model.response.login.LoginResponse
import com.dicoding.c_finance.model.response.GlobalResponse
import com.dicoding.c_finance.model.response.user.UsersItem
import kotlinx.coroutines.flow.firstOrNull

class FinanceRepository private constructor(
    var apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun login(username: String, password: String): Result<LoginResponse> {
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

    suspend fun getUsers(): Result<List<UsersItem>> {
        return try {
            val response = apiService.getUsers()
            if (response.status == "error") {
                Result.failure(Exception("Failed to get user data"))
            } else {
                Result.success(response.users)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addUser(
        nama: String,
        username: String,
        password: String,
        phone: String,
        id_role: Int
    ): Result<GlobalResponse> {
        return try {
            val response = apiService.addUser(nama, username, password, phone, id_role)
            if (response.status == "error") {
                Result.failure(Exception("Failed to add user"))
            } else {
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(
        id: Int,
        nama: String,
        username: String,
        password: String,
        phone: String,
        id_role: Int
    ): Result<GlobalResponse> {
        return try {
            val response = apiService.updateUser(id, nama, username, password, phone, id_role)
            if (response.status == "error") {
                Result.failure(Exception("Failed to update user"))
            } else {
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(id: Int): Result<GlobalResponse> {
        return try {
            val response = apiService.deleteUser(id)
            if (response.status == "error") {
                Result.failure(Exception("Failed to delete user"))
            } else {
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCashFlow(id_tipe: Int, id_kategori: Int, nominal: Int, tanggal: String, deskripsi: String):Result<GlobalResponse>{
        return try{
            val response = apiService.addTransaction(id_tipe, id_kategori, nominal, deskripsi, tanggal)
            if(response.status == "error"){
                Result.failure(Exception("Failed to add cashflow"))
            }else{
                Result.success(response)
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun updateCashflow(id_transaksi: Int, id_tipe: Int, id_kategori: Int, nominal: Int, deskripsi: String, tanggal: String):Result<GlobalResponse>{
        return try{
            val response = apiService.updateTransaction(id_transaksi, id_tipe, id_kategori, nominal, deskripsi, tanggal)
            if(response.status == "error"){
                Result.failure(Exception("Failed to update cashflow"))
            }else{
                Result.success(response)
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getCashflows():Result<List<TransaksiItem>>{
        return try {
            val response = apiService.getTransaction()
            if (response.status == "error") {
                Result.failure(Exception("Failed to get cashflow data"))
            } else {
                Result.success(response.transaksi)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCashflow(id: Int): Result<GlobalResponse> {
        return try {
            val response = apiService.deleteTransaction(id)
            if (response.status == "error") {
                Result.failure(Exception("Failed to delete cashflow"))
            } else {
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryByType(id: Int): Result<List<CategoryItem>>{
        return try {
            val response = apiService.getCategorybyType(id)
            if (response.status == "error") {
                Result.failure(Exception("Failed to get category data"))
            } else {
                Result.success(response.category)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var instance: FinanceRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): FinanceRepository =
            instance ?: synchronized(this) {
                instance ?: FinanceRepository(apiService, userPreference)
            }.also { instance = it }
    }
}