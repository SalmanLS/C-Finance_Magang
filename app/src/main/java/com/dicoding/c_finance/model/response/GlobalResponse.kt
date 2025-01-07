package com.dicoding.c_finance.model.response

import com.google.gson.annotations.SerializedName

data class GlobalResponse(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)
