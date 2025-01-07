package com.dicoding.c_finance.model.api

import com.dicoding.c_finance.model.response.cashflow.GetCashflowResponse
import com.dicoding.c_finance.model.response.category.GetCategoryResponse
import com.dicoding.c_finance.model.response.GlobalResponse
import com.dicoding.c_finance.model.response.user.GetUserResponse
import com.dicoding.c_finance.model.response.login.LoginResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("user/add")
    suspend fun addUser(
        @Field("nama") nama : String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("no_hp") no_hp: String,
        @Field("id_role") id_role: Int
    ): GlobalResponse

    @FormUrlEncoded
    @PUT("user/update")
    suspend fun updateUser(
        @Field("id_user") id: Int,
        @Field("nama") nama : String,
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

    @GET("category/read")
    suspend fun getCategorybyType(
        @Query("id_tipe") id_tipe: Int
    ): GetCategoryResponse

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
}