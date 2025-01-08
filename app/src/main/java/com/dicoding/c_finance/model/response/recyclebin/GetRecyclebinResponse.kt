package com.dicoding.c_finance.model.response.recyclebin

import com.google.gson.annotations.SerializedName

data class GetRecyclebinResponse(

	@field:SerializedName("recycle_bin")
	val recycleBin: List<RecycleBinItem> = emptyList(),

	@field:SerializedName("status")
	val status: String? = null
)

data class RecycleBinItem(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("id_kategori")
	val idKategori: String? = null,

	@field:SerializedName("nominal")
	val nominal: String? = null,

	@field:SerializedName("tanggal_transaksi")
	val tanggalTransaksi: String? = null,

	@field:SerializedName("tanggal_dihapus")
	val tanggalDihapus: String? = null,

	@field:SerializedName("nama_tipe")
	val namaTipe: String? = null,

	@field:SerializedName("id_transaksi")
	val idTransaksi: String? = null,

	@field:SerializedName("id_user")
	val idUser: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("id_tipe")
	val idTipe: String? = null,

	@field:SerializedName("id_recycle_bin")
	val idRecycleBin: String? = null,

	@field:SerializedName("nama_kategori")
	val namaKategori: String? = null
)
