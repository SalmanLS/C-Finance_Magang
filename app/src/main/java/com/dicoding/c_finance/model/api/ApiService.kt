package com.dicoding.c_finance.model.api

import com.dicoding.c_finance.model.response.cashflow.GetCashflowResponse
import com.dicoding.c_finance.model.response.category.GetCategoryResponse
import com.dicoding.c_finance.model.response.GlobalResponse
import com.dicoding.c_finance.model.response.log.GetLogResponse
import com.dicoding.c_finance.model.response.user.GetUserResponse
import com.dicoding.c_finance.model.response.login.LoginResponse
import com.dicoding.c_finance.model.response.recyclebin.GetRecyclebinResponse
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    //LOGIN
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    //USER
    @FormUrlEncoded
    @POST("user/add")
    suspend fun addUser(
        @Field("nama") nama: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("no_hp") no_hp: String,
        @Field("id_role") id_role: Int
    ): GlobalResponse

    @FormUrlEncoded
    @PUT("user/update")
    suspend fun updateUser(
        @Field("id_user") id: Int,
        @Field("nama") nama: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("no_hp") no_hp: String,
        @Field("id_role") id_role: Int
    ): GlobalResponse

    @GET("user/read")
    suspend fun getUsers(): GetUserResponse

    @DELETE("user/delete")
    suspend fun deleteUser(
        @Query("id_user") id_user: Int
    ): GlobalResponse

    //CATEGORY
    @GET("category/read")
    suspend fun getCategorybyType(
        @Query("id_tipe") id_tipe: Int
    ): GetCategoryResponse

    @FormUrlEncoded
    @POST("category/add")
    suspend fun addCategory(
        @Field("id_tipe") id_tipe: Int,
        @Field("nama_kategori") nama_kategori: String
    ): GlobalResponse

    @FormUrlEncoded
    @PUT("category/update")
    suspend fun updateCategory(
        @Field("id_kategori") id_kategori: Int,
        @Field("id_tipe") id_tipe: Int,
        @Field("nama_kategori") nama_kategori: String
    ): GlobalResponse

    @DELETE("category/delete")
    suspend fun deleteCategory(
        @Query("id_kategori") id_kategori: Int
    ): GlobalResponse

    //TRANSACTION
    @FormUrlEncoded
    @POST("transaction/add")
    suspend fun addTransaction(
        @Field("id_tipe") id_tipe: Int,
        @Field("id_kategori") id_kategori: Int,
        @Field("nominal") nominal: Int,
        @Field("deskripsi") deskripsi: String,
        @Field("tanggal_transaksi") tanggal_transaksi: String
    ): GlobalResponse

    @FormUrlEncoded
    @PUT("transaction/update")
    suspend fun updateTransaction(
        @Field("id_transaksi") id_transaksi: Int,
        @Field("id_tipe") id_tipe: Int,
        @Field("id_kategori") id_kategori: Int,
        @Field("nominal") nominal: Int,
        @Field("deskripsi") deskripsi: String,
        @Field("tanggal_transaksi") tanggal_transaksi: String
    ): GlobalResponse

    @GET("transaction/read")
    suspend fun getTransaction(): GetCashflowResponse

    @DELETE("transaction/delete")
    suspend fun deleteTransaction(
        @Query("id_transaksi") id_transaksi: Int
    ): GlobalResponse

    @GET("transaction/recent")
    suspend fun getRecentTransaction(): GetCashflowResponse

    //LOG
    @GET("actlog/read")
    suspend fun getLogs(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetLogResponse

    @DELETE("actlog/delete")
    suspend fun deleteLog(
        @Query("id") id: Int
    ): GlobalResponse

    //RECYCLE_BIN

    @GET("recyclebin/read")
    suspend fun getRecycleBin(): GetRecyclebinResponse

    @DELETE("recyclebin/delete")
    suspend fun deleteRecycleBin(
        @Query("id_recycle_bin") id_recycle_bin: Int
    ): GlobalResponse

    @GET("export")
    suspend fun exportData(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String
    ): ResponseBody
}