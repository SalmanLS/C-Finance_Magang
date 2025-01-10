package com.dicoding.c_finance.view.cashflow

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.FragmentCashflowBinding
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import com.dicoding.c_finance.utils.CashflowAdapter
import com.dicoding.c_finance.view.cashflow.viewmodel.CashflowViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CashflowFragment : Fragment() {
    private lateinit var binding: FragmentCashflowBinding
    private lateinit var cashflowAdapter: CashflowAdapter
    private var beginDate: String? = null
    private var endDate: String? = null
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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
        setupChipGroup()
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
            applyFilter(cashflow, binding.chipGroupFilter.checkedChipId)
        }

        cashflowViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun setupChipGroup() {
        binding.filterAll.isChecked = true
        binding.chipGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            val cashflow = cashflowViewModel.cashflowData.value ?: emptyList()
            when (checkedId) {
                R.id.filterDate -> showDatePickers()
                else -> applyFilter(cashflow, checkedId)
            }
        }
    }

    private fun showDatePickers() {
        val calendar = Calendar.getInstance()

        val beginDatePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedBeginDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                beginDate = dateFormatter.format(selectedBeginDate.time)

                val endDatePicker = DatePickerDialog(
                    requireContext(),
                    { _, endYear, endMonth, endDayOfMonth ->
                        val selectedEndDate = Calendar.getInstance().apply {
                            set(endYear, endMonth, endDayOfMonth)
                        }
                        endDate = dateFormatter.format(selectedEndDate.time)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.filtered_from_to, beginDate, endDate),
                            Toast.LENGTH_LONG
                        ).show()
                        val cashflow = cashflowViewModel.cashflowData.value ?: emptyList()
                        applyDateFilter(cashflow)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                endDatePicker.setOnCancelListener {
                    resetToDefaultFilter()
                }

                endDatePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        beginDatePicker.setOnCancelListener {
            resetToDefaultFilter()
        }

        beginDatePicker.show()
    }

    private fun resetToDefaultFilter() {
        binding.filterAll.isChecked = true
        val cashflow = cashflowViewModel.cashflowData.value ?: emptyList()
        applyFilter(cashflow, R.id.filterAll)
    }

    private fun applyDateFilter(cashflow: List<TransaksiItem>) {
        val filteredCashflow = cashflow.filter { transaction ->
            val transactionDate = try {
                dateFormatter.parse(transaction.tanggalTransaksi!!)
            } catch (e: Exception) {
                null
            }
            transactionDate?.let {
                beginDate?.let { begin -> it >= dateFormatter.parse(begin) } == true &&
                        endDate?.let { end -> it <= dateFormatter.parse(end) } == true
            } ?: false
        }
        cashflowAdapter.submitList(filteredCashflow)
    }

    private fun applyFilter(cashflow: List<TransaksiItem>, filterId: Int) {
        when (filterId) {
            R.id.filterAll -> {
                cashflowAdapter.submitList(cashflow)
            }
            R.id.filterIncome -> {
                val incomeCashflow = cashflow.filter { it.namaTipe == "Pemasukan" }
                cashflowAdapter.submitList(incomeCashflow)
            }
            R.id.filterExpense -> {
                val expenseCashflow = cashflow.filter { it.namaTipe == "Pengeluaran" }
                cashflowAdapter.submitList(expenseCashflow)
            }
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
                .setMessage("Are you sure you want to delete this transaction?")
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
