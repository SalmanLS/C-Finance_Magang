package com.dicoding.c_finance.model.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.c_finance.model.api.ApiConfig
import com.dicoding.c_finance.model.api.ApiService
import com.dicoding.c_finance.model.pref.UserPreference
import com.dicoding.c_finance.model.pref.UserToken
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import com.dicoding.c_finance.model.response.category.CategoryItem
import com.dicoding.c_finance.model.response.login.LoginResponse
import com.dicoding.c_finance.model.response.GlobalResponse
import com.dicoding.c_finance.model.response.log.LogItem
import com.dicoding.c_finance.model.response.recyclebin.RecycleBinItem
import com.dicoding.c_finance.model.response.user.UsersItem
import com.dicoding.c_finance.utils.LogPagingSource
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

    suspend fun getCategory(): Result<List<CategoryItem>>{
        return try {
            val response = apiService.getCategory()
            if (response.status == "error") {
                Result.failure(Exception("Failed to get category data"))
            } else {
                Result.success(response.category)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCategory(id_tipe: Int, nama_kategori: String): Result<GlobalResponse>{
        return try{
            val response = apiService.addCategory(id_tipe, nama_kategori)
            if(response.status == "error"){
                Result.failure(Exception("Failed to add category"))
            }else{
                Result.success(response)
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun updateCategory(id_kategori: Int, id_tipe: Int, nama_kategori: String): Result<GlobalResponse>{
        return try{
            val response = apiService.updateCategory(id_kategori, id_tipe, nama_kategori)
            if(response.status == "error"){
                Result.failure(Exception("Failed to update category"))
            }else{
                Result.success(response)
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(id_kategori: Int): Result<GlobalResponse>{
        return try{
            val response = apiService.deleteCategory(id_kategori)
            if(response.status == "error"){
                Result.failure(Exception("Failed to delete category"))
            }else{
                Result.success(response)
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    fun getLogs(): LiveData<PagingData<LogItem>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                LogPagingSource(apiService)
            }
        ).liveData
    }

    suspend fun deleteLogs(id: Int): Result<GlobalResponse>{
        return try{
            val response = apiService.deleteLog(id)
            if(response.status == "error"){
                Result.failure(Exception("Failed to delete log"))
            }else{
                Result.success(response)
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getRecycleBin(): Result<List<RecycleBinItem>>{
        return try {
            val response = apiService.getRecycleBin()
            if (response.status == "error") {
                Result.failure(Exception("Failed to get recycle bin data"))
            } else {
                Result.success(response.recycleBin)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRecycleBin(id: Int): Result<GlobalResponse>{
        return try{
            val response = apiService.deleteRecycleBin(id)
            if(response.status == "error"){
                Result.failure(Exception("Failed to delete recycle bin"))
            }else{
                Result.success(response)
            }
        }catch (e: Exception){
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