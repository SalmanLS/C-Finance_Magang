package com.dicoding.c_finance.model.response.cashflow

import com.google.gson.annotations.SerializedName

data class GetCashflowResponse(

    @field:SerializedName("transaksi")
    val transaksi: List<TransaksiItem> = emptyList(),

    @field:SerializedName("status")
    val status: String? = null
)

data class TransaksiItem(

    @field:SerializedName("id_kategori")
    val idKategori: String? = null,

    @field:SerializedName("nominal")
    val nominal: String? = null,

    @field:SerializedName("tanggal_transaksi")
    val tanggalTransaksi: String? = null,

    @field:SerializedName("tanggal_dihapus")
    val tanggalDihapus: Any? = null,

    @field:SerializedName("id_transaksi")
    val idTransaksi: String? = null,

    @field:SerializedName("id_user")
    val idUser: String? = null,

    @field:SerializedName("deskripsi")
    val deskripsi: String? = null,

    @field:SerializedName("id_tipe")
    val idTipe: String? = null
)
