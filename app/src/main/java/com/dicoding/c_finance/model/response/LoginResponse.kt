package com.dicoding.c_finance.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("token")
	val token: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
