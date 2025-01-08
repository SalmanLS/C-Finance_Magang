package com.dicoding.c_finance.model.response.log

import com.google.gson.annotations.SerializedName

data class GetLogResponse(

	@field:SerializedName("pagination")
	val pagination: Pagination? = null,

	@field:SerializedName("log")
	val log: List<LogItem> = emptyList(),

	@field:SerializedName("status")
	val status: String? = null
)

data class Pagination(

	@field:SerializedName("total_records")
	val totalRecords: String? = null,

	@field:SerializedName("total_pages")
	val totalPages: Int? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null,

	@field:SerializedName("page_size")
	val pageSize: Int? = null
)

data class LogItem(

	@field:SerializedName("action_type")
	val actionType: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("id_user")
	val idUser: String? = null
)
