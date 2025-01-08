package com.dicoding.c_finance.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.c_finance.databinding.LogItemBinding
import com.dicoding.c_finance.model.response.log.LogItem

class PagingLogAdapter(private val onDeleteClick: (Int) -> Unit) :
    PagingDataAdapter<LogItem, PagingLogAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val log = getItem(position)
        if (log != null) {
            holder.bind(log)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LogItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    inner class MyViewHolder(private val binding: LogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(log: LogItem) {
            binding.tvActionType.text = log.actionType
            val date = log.createdAt?.let {
                formatTimestamp(
                    it,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd MMM yyyy, hh:mm a"
                )
            }
            binding.tvDate.text = date
            binding.tvDescription.text = log.description
            binding.btnDelete.setOnClickListener {
                onDeleteClick(log.id.toString().toInt())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LogItem>() {
            override fun areItemsTheSame(oldItem: LogItem, newItem: LogItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: LogItem, newItem: LogItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }


}