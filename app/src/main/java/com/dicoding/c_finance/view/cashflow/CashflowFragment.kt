package com.dicoding.c_finance.view.cashflow

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.FragmentCashflowBinding
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import com.dicoding.c_finance.utils.CashflowAdapter
import com.dicoding.c_finance.view.cashflow.viewmodel.CashflowViewModel

class CashflowFragment : Fragment() {
    private lateinit var binding: FragmentCashflowBinding
    private lateinit var cashflowAdapter: CashflowAdapter
    private val cashflowViewModel by viewModels<CashflowViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val addDataLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            cashflowViewModel.fetchCashflow()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCashflowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        cashflowAdapter = CashflowAdapter { selectedCashflow ->
            showCashflowDetails(selectedCashflow)
        }

        binding.rvCashflow.adapter = cashflowAdapter
        binding.rvCashflow.layoutManager = LinearLayoutManager(context)

        binding.btnAddCashflow.setOnClickListener {
            goToCashflowAddUpdate()
        }

        if (cashflowViewModel.cashflowData.value == null) {
            cashflowViewModel.fetchCashflow()
        }

        cashflowViewModel.cashflowData.observe(viewLifecycleOwner) { cashflow ->
            cashflowAdapter.submitList(cashflow)
        }

        cashflowViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun showCashflowDetails(cashflow: TransaksiItem) {
        val dialog = CashflowDetailDialogFragment.newInstance(cashflow)
        dialog.setOnEditListener {
            val intent = Intent(requireContext(), CashflowAddUpdateActivity::class.java)
            intent.putExtra(TRANSACTION_ID, cashflow)
            addDataLauncher.launch(intent)
        }
        dialog.setOnDeleteListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this cashflow?")
                .setPositiveButton("Yes") { _, _ ->
                    cashflow.idTransaksi?.let { it1 -> cashflowViewModel.deleteCashflow(it1) }
                }
                .setNegativeButton("No", null)
                .show()
        }
        dialog.show(parentFragmentManager, "CashflowDetailDialog")
    }


    private fun goToCashflowAddUpdate() {
        val intent = Intent(requireContext(), CashflowAddUpdateActivity::class.java)
        addDataLauncher.launch(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressIndicator.visibility = View.VISIBLE
        } else {
            binding.progressIndicator.visibility = View.GONE
        }
    }

    companion object {
        const val TRANSACTION_ID = "id_transaksi"
    }
}