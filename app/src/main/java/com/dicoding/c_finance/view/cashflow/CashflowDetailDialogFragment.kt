package com.dicoding.c_finance.view.cashflow

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.dicoding.c_finance.R
import com.dicoding.c_finance.databinding.FragmentCashflowDetailDialogBinding
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import com.dicoding.c_finance.model.response.user.UsersItem
import com.dicoding.c_finance.utils.customCurrencyFormat
import com.dicoding.c_finance.utils.customDateFormat

class CashflowDetailDialogFragment : DialogFragment() {
    private var _binding: FragmentCashflowDetailDialogBinding? = null
    private val binding get() = _binding!!
    private var onEditListener: ((TransaksiItem) -> Unit)? = null
    private var onDeleteListener: ((TransaksiItem) -> Unit)? = null

    companion object {
        private const val ARG_CASHFLOW = "cashflow"

        fun newInstance(cashflow: TransaksiItem): CashflowDetailDialogFragment {
            val args = Bundle().apply {
                putParcelable(ARG_CASHFLOW, cashflow)
            }
            return CashflowDetailDialogFragment().apply { arguments = args }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentCashflowDetailDialogBinding.inflate(layoutInflater)
        val cashflow = arguments?.getParcelable<TransaksiItem>(ARG_CASHFLOW)

        val date = cashflow?.tanggalTransaksi?.let { customDateFormat(it) }
        binding.tvUsername.text = cashflow?.nama ?: "-"
        binding.tvDate.text = date ?: "-"
        binding.tvDate.setTextColor(resources.getColor(R.color.blue_text))
        val nominal = cashflow?.nominal?.let { customCurrencyFormat(it.toDouble()) }
        if (cashflow?.idTipe == 1) {
            binding.tvAmount.setTextColor(resources.getColor(R.color.green_text))
            binding.tvAmount.text = nominal
        } else {
            binding.tvAmount.setTextColor(resources.getColor(R.color.red))
            binding.tvAmount.text = nominal
        }
        binding.tvCategory.text = cashflow?.namaKategori ?: "-"
        binding.tvType.text = if (cashflow?.idTipe == 1) "Income" else "Expense"
        binding.tvDescription.text = cashflow?.deskripsi ?: "-"

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Edit") { _, _ ->
                cashflow?.let { onEditListener?.invoke(it) }
            }
            .setNegativeButton("Delete") { _, _ ->
                cashflow?.let { onDeleteListener?.invoke(it) }
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    fun setOnEditListener(listener: (TransaksiItem) -> Unit) {
        onEditListener = listener
    }

    fun setOnDeleteListener(listener: (TransaksiItem) -> Unit) {
        onDeleteListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
