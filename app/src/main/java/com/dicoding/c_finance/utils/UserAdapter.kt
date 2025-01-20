package com.dicoding.c_finance.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.c_finance.databinding.UserItemBinding
import com.dicoding.c_finance.model.response.user.UsersItem

class UserAdapter(private val onUserClicked: (UsersItem) -> Unit) :
    ListAdapter<UsersItem, UserAdapter.MyViewHolder>(DIFF_CALLBACK), Filterable {

    private val originalList = mutableListOf<UsersItem>()
    private var filteredList: List<UsersItem> = listOf()

    init {
        filteredList = currentList
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = filteredList[position]
        holder.bind(user)
    }

    override fun submitList(list: List<UsersItem>?) {
        super.submitList(list)
        list?.let {
            originalList.clear()
            originalList.addAll(it)
            filteredList = it
        }
    }

    inner class MyViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UsersItem) {
            binding.tvName.text = user.nama
            binding.tvPhone.text = user.noHp
            binding.root.setOnClickListener {
                onUserClicked(user)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UsersItem>() {
            override fun areItemsTheSame(oldItem: UsersItem, newItem: UsersItem): Boolean {
                return oldItem.idUser == newItem.idUser
            }

            override fun areContentsTheSame(oldItem: UsersItem, newItem: UsersItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim() ?: ""
                val filtered = if (query.isEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.nama?.contains(query, ignoreCase = true) == true
                    }
                }
                return FilterResults().apply { values = filtered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<UsersItem>
                notifyDataSetChanged()
            }
        }
    }
}

