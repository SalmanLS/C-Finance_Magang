package com.dicoding.c_finance.view.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.FragmentHomeBinding
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import com.dicoding.c_finance.utils.CashflowAdapter
import com.dicoding.c_finance.utils.customCurrencyFormat
import com.dicoding.c_finance.view.cashflow.CashflowFragment
import com.dicoding.c_finance.view.home.viewmodel.HomeViewModel
import com.dicoding.c_finance.view.login.LoginActivity
import ir.mahozad.android.PieChart
import ir.mahozad.android.component.Alignment
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var cashflowAdapter: CashflowAdapter
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun observeViewModel() {
        if (viewModel.cashflowData.value == null) {
            viewModel.fetchCashflow()
        }
        if (viewModel.cashflowDataRecent.value == null) {
            viewModel.fetchRecentCashflow()
        }
        viewModel.cashflowData.observe(viewLifecycleOwner) { cashflow ->
            setHeaderView(cashflow)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        cashflowAdapter = CashflowAdapter({
            return@CashflowAdapter
        }, enableFiltering = false)
        binding.rvRecentTransaction.adapter = cashflowAdapter
        binding.rvRecentTransaction.layoutManager = LinearLayoutManager(context)

        viewModel.cashflowDataRecent.observe(viewLifecycleOwner) { cashflow ->
            cashflowAdapter.submitList(cashflow)
        }
        binding.tvShowAll.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.flFragment, CashflowFragment())
                .addToBackStack(null)
                .commit()
            val bottomNav =
                requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bnView
                )
            bottomNav.selectedItemId = R.id.nav_list
        }
    }

    private fun updatePieChart(cashflow: List<TransaksiItem>) {
        val formatterMonthString = SimpleDateFormat("MMMM", Locale.getDefault())
        val formatterMonth = SimpleDateFormat("MM", Locale.getDefault())
        val formatterYear = SimpleDateFormat("yyyy", Locale.getDefault())
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val currentDate = Date(System.currentTimeMillis())
        val currentMonth = formatterMonth.format(currentDate)
        val currentYear = formatterYear.format(currentDate)

        val monthlyCashflow = cashflow.filter {
            val transactionDate = try {
                it.tanggalTransaksi?.let { date -> inputFormatter.parse(date) }
            } catch (e: Exception) {
                null
            }
            transactionDate != null && formatterMonth.format(transactionDate) == currentMonth &&
                    formatterYear.format(transactionDate) == currentYear
        }

        var totalIncome = 0.0
        var totalExpense = 0.0
        for (transaction in monthlyCashflow) {
            if (transaction.namaTipe == "Pemasukan") {
                totalIncome += transaction.nominal!!.toDouble()
            } else {
                totalExpense += transaction.nominal!!.toDouble()
            }
        }

        val total = totalIncome + totalExpense
        val incomeRatio = if (total > 0) (totalIncome / total).toFloat() else 0f
        val expenseRatio = if (total > 0) (totalExpense / total).toFloat() else 0f

        binding.pieChart.apply {
            slices = listOf(
                PieChart.Slice(incomeRatio, resources.getColor(R.color.green_text)),
                PieChart.Slice(expenseRatio, resources.getColor(R.color.red))
            )
            holeRatio = 0.7f
            isLegendEnabled = false
            labelType = PieChart.LabelType.NONE
            startAngle = 0
        }
        binding.tvMonthNow.text = formatterMonthString.format(currentDate)
        binding.tvIncomeMonthly.text = customCurrencyFormat(totalIncome)
        binding.tvOutcomeMonthly.text = customCurrencyFormat(totalExpense)
        binding.tvBalanceMonthly.text = customCurrencyFormat(totalIncome - totalExpense)
    }


    private fun setHeaderView(cashflow: List<TransaksiItem>) {
        var total = 0
        var totalPengeluaran = 0.0
        var totalPemasukan = 0.0
        for (i in cashflow.indices) {
            if (cashflow[i].namaTipe == "Pemasukan") {
                total += cashflow[i].nominal!!.toInt()
            } else {
                total -= cashflow[i].nominal!!.toInt()
            }
        }
        val newTotal = total.toDouble()
        binding.tvBalance.text = customCurrencyFormat(newTotal)
        for (i in cashflow.indices) {
            if (cashflow[i].namaTipe == "Pemasukan") {
                totalPemasukan += cashflow[i].nominal!!.toDouble()
            } else {
                totalPengeluaran += cashflow[i].nominal!!.toDouble()
            }
        }
        val newPengeluaran = customCurrencyFormat(totalPengeluaran)
        val newPemasukan = customCurrencyFormat(totalPemasukan)
        binding.tvIncome.text = newPemasukan
        binding.tvOutcome.text = newPengeluaran

        val currentDate = Date(System.currentTimeMillis())
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val formattedDate = formatter.format(currentDate)
        val newDate = formatter.parse(formattedDate)?.let { outputFormat.format(it) }
        binding.tvDateNow.text = newDate

        updatePieChart(cashflow)

    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        viewModel.logout()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressIndicator.visibility = View.VISIBLE
            binding.mainContainer.children.forEach {
                it.visibility = View.GONE
            }
            binding.clInfo.visibility = View.GONE
            binding.pieChart.visibility = View.GONE
        } else {
            binding.progressIndicator.visibility = View.GONE
            binding.mainContainer.children.forEach {
                it.visibility = View.VISIBLE
            }
            binding.clInfo.visibility = View.VISIBLE
            binding.pieChart.visibility = View.VISIBLE
        }
    }

}