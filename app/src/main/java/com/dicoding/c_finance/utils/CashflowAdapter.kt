package com.dicoding.c_finance.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.c_finance.R
import com.dicoding.c_finance.databinding.CashflowItemBinding
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem

class CashflowAdapter(
    private val onItemClick: (TransaksiItem) -> Unit,
    private val enableFiltering: Boolean = false
) : ListAdapter<TransaksiItem, CashflowAdapter.MyViewHolder>(DIFF_CALLBACK), Filterable {

    private val originalList = mutableListOf<TransaksiItem>()
    private var filteredList: List<TransaksiItem> = listOf()

    init {
        filteredList = currentList
    }

    override fun getItemCount(): Int = if (enableFiltering) filteredList.size else super.getItemCount()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CashflowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val transaksiItem = if (enableFiltering) filteredList[position] else getItem(position)
        holder.bind(transaksiItem)
    }

    override fun submitList(list: List<TransaksiItem>?) {
        super.submitList(list)
        list?.let {
            originalList.clear()
            originalList.addAll(it)
            filteredList = it
        }
    }

    inner class MyViewHolder(private val binding: CashflowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaksiItem: TransaksiItem) {
            binding.tvUsername.text = transaksiItem.nama
            val tanggal = transaksiItem.tanggalTransaksi?.let { customDateFormat(it) }
            binding.tvDate.text = tanggal
            val nominal = transaksiItem.nominal?.toDouble()
            val newNominal = nominal?.let { customCurrencyFormat(it) }
            if (transaksiItem.idTipe == 1) {
                binding.tvTotal.setTextColor(binding.root.resources.getColor(R.color.green_text))
            } else {
                binding.tvTotal.setTextColor(binding.root.resources.getColor(R.color.red))
            }
            binding.tvTotal.text = newNominal
            binding.tvCategory.text = transaksiItem.namaKategori
            binding.tvType.text = if (transaksiItem.idTipe == 1) "Income" else "Expense"
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                if (!enableFiltering) return FilterResults() // Skip filtering if disabled
                val query = constraint?.toString()?.trim() ?: ""
                val filtered = if (query.isEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.nama?.contains(query, ignoreCase = true) == true ||
                                it.namaKategori?.contains(query, ignoreCase = true) == true
                    }
                }
                return FilterResults().apply { values = filtered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (enableFiltering) {
                    filteredList = results?.values as List<TransaksiItem>
                    notifyDataSetChanged()
                }
            }
        }
    }
}
