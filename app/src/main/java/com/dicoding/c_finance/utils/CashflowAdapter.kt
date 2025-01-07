package com.dicoding.c_finance.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.c_finance.databinding.CashflowItemBinding
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem

class CashflowAdapter(private val onItemClick: (TransaksiItem) -> Unit) : ListAdapter<TransaksiItem, CashflowAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CashflowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val transaksiItem = getItem(position)
        holder.bind(transaksiItem)
    }

    inner class MyViewHolder(private val binding: CashflowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaksiItem: TransaksiItem) {
            binding.tvUsername.text = transaksiItem.nama
            val tanggal = transaksiItem.tanggalTransaksi?.let { customDateFormat(it) }
            binding.tvDate.text = tanggal
            val nominal = transaksiItem.nominal?.toDouble()
            val newNominal = nominal?.let { customCurrencyFormat(it) }
            binding.tvTotal.text = newNominal
            binding.tvCategory.text = transaksiItem.namaKategori
            binding.tvType.text = transaksiItem.namaTipe
            binding.root.setOnClickListener { onItemClick(transaksiItem) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransaksiItem>() {
            override fun areItemsTheSame(oldItem: TransaksiItem, newItem: TransaksiItem): Boolean {
                return oldItem.idTransaksi == newItem.idTransaksi
            }

            override fun areContentsTheSame(
                oldItem: TransaksiItem,
                newItem: TransaksiItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }


}