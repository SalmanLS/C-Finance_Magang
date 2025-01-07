package com.dicoding.c_finance.model.response.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetCategoryResponse(

	@field:SerializedName("category")
	val category: List<CategoryItem> = emptyList(),

	@field:SerializedName("status")
	val status: String? = null
)

@Parcelize
data class CategoryItem(

	@field:SerializedName("id_kategori")
	val idKategori: Int? = null,

	@field:SerializedName("id_tipe")
	val idTipe: Int? = null,

	@field:SerializedName("nama_kategori")
	val namaKategori: String? = null
) : Parcelable
