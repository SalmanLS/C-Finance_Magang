package com.dicoding.c_finance.view.cashflow

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.FragmentCashflowBinding
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import com.dicoding.c_finance.utils.CashflowAdapter
import com.dicoding.c_finance.utils.ExportState
import com.dicoding.c_finance.utils.saveFileToDownloads
import com.dicoding.c_finance.view.cashflow.viewmodel.CashflowViewModel
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CashflowFragment : Fragment() {
    private lateinit var binding: FragmentCashflowBinding
    private lateinit var cashflowAdapter: CashflowAdapter
    private var beginDate: String? = null
    private var endDate: String? = null
    private var fileName: String? = null
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
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                beginDate?.let { start ->
                    endDate?.let { end ->
                        exportCashflow(start, end)
                    }
                }
            } else {
                showToast("Permission denied. Cannot save the file.")
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
        cashflowAdapter = CashflowAdapter(onItemClick = {
            showCashflowDetails(it)
        }, enableFiltering = true)

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
        setupSearch()

        binding.btnExport.setOnClickListener {
            exportDialog()
        }
    }

    private fun exportDialog() {
        val dialogFragment = ExportDialogFragment()
        dialogFragment.setExportDialogListener(object : ExportDialogFragment.ExportDialogListener {
            override fun onExportClicked(startDate: String, endDate: String) {
                checkPermissionAndExport(startDate, endDate)
            }
        })
        dialogFragment.show(parentFragmentManager, "ExportDialog")
    }

    private fun checkPermissionAndExport(startDate: String, endDate: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                exportCashflow(startDate, endDate)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            exportCashflow(startDate, endDate)
        }
    }

    private fun exportCashflow(startDate: String, endDate: String) {
        cashflowViewModel.exportData(startDate, endDate)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            cashflowViewModel.exportState.collect { state ->
                when (state) {
                    is ExportState.Idle -> { /* Do nothing */ }
                    is ExportState.Loading -> showLoading(true)
                    is ExportState.Success -> {
                        showLoading(false)
                        saveExportedFile(state.data)
                        Toast.makeText(requireContext(), "Export successful!", Toast.LENGTH_LONG).show()
                        sendDownloadCompleteNotification(fileName!!)
                    }
                    is ExportState.Failure -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), "Export failed: ${state.errorMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun saveExportedFile(responseBody: ResponseBody) {
        fileName = "transaction_report_${System.currentTimeMillis()}.pdf"
        val file = saveFileToDownloads(
            responseBody,
            fileName = fileName!!
        )
        if (file != null) {
            Toast.makeText(requireContext(), "File saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Failed to save file. Please try again.", Toast.LENGTH_LONG).show()
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

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener {
            val query = it.toString()
            cashflowAdapter.filter.filter(query)
        }
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
            binding.root.alpha = 0.5f
        } else {
            binding.progressIndicator.visibility = View.GONE
            binding.root.alpha = 1.0f
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun sendDownloadCompleteNotification(fileName: String) {
        val downloadIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)

        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            downloadIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setContentTitle("Download Complete")
            .setSmallIcon(R.drawable.baseline_file_download_done_24)
            .setContentText("File \"$fileName\" has been downloaded successfully.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val TRANSACTION_ID = "id_transaksi"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "transaction_channel"
    }
}
