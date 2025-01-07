package com.dicoding.c_finance.model.response.cashflow

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetCashflowResponse(

	@field:SerializedName("transaksi")
	val transaksi: List<TransaksiItem> = emptyList(),

	@field:SerializedName("status")
	val status: String? = null
)

@Parcelize
data class TransaksiItem(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("id_kategori")
	val idKategori: Int? = null,

	@field:SerializedName("nominal")
	val nominal: String? = null,

	@field:SerializedName("tanggal_transaksi")
	val tanggalTransaksi: String? = null,

	@field:SerializedName("tanggal_dihapus")
	val tanggalDihapus: String? = null,

	@field:SerializedName("nama_tipe")
	val namaTipe: String? = null,

	@field:SerializedName("id_transaksi")
	val idTransaksi: Int? = null,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("id_tipe")
	val idTipe: Int? = null,

	@field:SerializedName("nama_kategori")
	val namaKategori: String? = null
) : Parcelable
