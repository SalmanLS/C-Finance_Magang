package com.dicoding.c_finance.model.response.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetUserResponse(

    @field:SerializedName("users")
    val users: List<UsersItem> = emptyList(),

    @field:SerializedName("status")
    val status: String? = null
)

@Parcelize
data class UsersItem(
    @field:SerializedName("nama")
    val nama: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("no_hp")
    val noHp: String? = null,

    @field:SerializedName("id_role")
    val idRole: String? = null,

    @field:SerializedName("id_user")
    val idUser: String? = null,

    @field:SerializedName("username")
    val username: String? = null

) : Parcelable
