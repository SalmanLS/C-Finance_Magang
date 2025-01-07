package com.dicoding.c_finance.model.api

import com.dicoding.c_finance.model.response.cashflow.GetCashflowResponse
import com.dicoding.c_finance.model.response.user.UserResponse
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
    ): UserResponse

    @FormUrlEncoded
    @PUT("user/update")
    suspend fun updateUser(
        @Field("id_user") id: Int,
        @Field("nama") nama : String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("no_hp") no_hp: String,
        @Field("id_role") id_role: Int
    ): UserResponse

    @GET("user/read")
    suspend fun getUsers(): GetUserResponse

    @DELETE("user/delete")
    suspend fun deleteUser(
        @Query("id_user") id_user: Int
    ): UserResponse


//    @GET("transaction/read")
//    suspend fun getTransaction(): GetCashflowResponse
}