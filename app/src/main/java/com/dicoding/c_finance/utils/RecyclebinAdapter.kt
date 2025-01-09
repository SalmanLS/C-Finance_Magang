package com.dicoding.c_finance.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.c_finance.R
import com.dicoding.c_finance.databinding.CashflowRecycleBinding
import com.dicoding.c_finance.model.response.recyclebin.RecycleBinItem

class RecyclebinAdapter(): ListAdapter<RecycleBinItem, RecyclebinAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            CashflowRecycleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class MyViewHolder(private val binding: CashflowRecycleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recycleBinItem: RecycleBinItem) {
            binding.tvCategory.text = recycleBinItem.namaKategori
            binding.tvUsername.text = recycleBinItem.nama
            val date = recycleBinItem.tanggalTransaksi?.let { customDateFormat(it) }
            binding.tvDateCreated.text = date
            val dateDeleted = recycleBinItem.tanggalDihapus?.let { customDateFormat(it) }
            binding.tvDateDeleted.text = dateDeleted
            val nominal = recycleBinItem.nominal?.let { customCurrencyFormat(it.toDouble()) }
            if (recycleBinItem.idTipe == "1") {
                binding.tvTotal.setTextColor(binding.root.resources.getColor(R.color.green_text))
            } else {
                binding.tvTotal.setTextColor(binding.root.resources.getColor(R.color.red))
            }
            binding.tvTotal.text = nominal
            binding.tvType.text = recycleBinItem.namaTipe

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecycleBinItem>() {
            override fun areItemsTheSame(oldItem: RecycleBinItem, newItem: RecycleBinItem): Boolean {
                return oldItem.idRecycleBin == newItem.idRecycleBin
            }

            override fun areContentsTheSame(oldItem: RecycleBinItem, newItem: RecycleBinItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}