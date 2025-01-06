//package com.dicoding.c_finance.utils
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.dicoding.c_finance.databinding.CashflowItemBinding
//import com.dicoding.c_finance.databinding.UserItemBinding
//import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
//import com.dicoding.c_finance.model.response.user.UsersItem
//
//class CashflowAdapter(): ListAdapter<TransaksiItem, UserAdapter.MyViewHolder>(DIFF_CALLBACK) {
//
//
//    class MyViewHolder(private val binding: CashflowItemBinding): RecyclerView.ViewHolder(binding.root){
//        fun bind(transaksiItem: TransaksiItem){
//            binding.tvUsername.text = transaksiItem.
//            binding.tvDate.text =
//        }
//    }
//    companion object {
//        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UsersItem>() {
//            override fun areItemsTheSame(
//                oldItem: UsersItem,
//                newItem: UsersItem
//            ): Boolean {
//                return oldItem.idUser == newItem.idUser
//            }
//
//            override fun areContentsTheSame(
//                oldItem: UsersItem,
//                newItem: UsersItem
//            ): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//}