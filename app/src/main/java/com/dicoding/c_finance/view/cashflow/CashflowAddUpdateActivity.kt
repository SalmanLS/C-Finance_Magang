package com.dicoding.c_finance.view.cashflow

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.ActivityCashflowAddUpdateBinding
import com.dicoding.c_finance.model.response.cashflow.TransaksiItem
import com.dicoding.c_finance.model.response.category.CategoryItem
import com.dicoding.c_finance.view.cashflow.viewmodel.CashflowAddUpdateViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CashflowAddUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCashflowAddUpdateBinding
    private val viewModel by viewModels<CashflowAddUpdateViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var id_transaksi: Int? = null
    private var id_type: Int? = null
    private var selectedCategoryId: Int? = null
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashflowAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        disableFormFields()

        val cashflowItem: TransaksiItem? =
            intent.getParcelableExtra(CashflowFragment.TRANSACTION_ID)
        isEdit = cashflowItem != null

        binding.tfCategory.boxBackgroundColor = getColor(R.color.transparant)
        binding.autoCompleteTextView.setBackgroundColor(getColor(R.color.transparant))

        if (isEdit) {
            id_transaksi = cashflowItem?.idTransaksi
            binding.tiDescription.setText(cashflowItem?.deskripsi)
            binding.tiNominal.setText(cashflowItem?.nominal.toString())
            binding.edtDate.setText(cashflowItem?.tanggalTransaksi)
            id_type = cashflowItem?.idTipe
            selectedCategoryId = cashflowItem?.idKategori
            id_type?.let {
                val radioButton = binding.radioGroup.getChildAt(it - 1) as? RadioButton
                radioButton?.isChecked = true
                fetchCategoryAndEnableForm(it)
            }

            binding.autoCompleteTextView.setText(cashflowItem?.namaKategori, false)
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            id_type = when (checkedId) {
                R.id.radioIncome -> 1
                R.id.radioExpense -> 2
                else -> null
            }

            id_type?.let {
                binding.autoCompleteTextView.setText("")
                selectedCategoryId = null

                fetchCategoryAndEnableForm(it)
            }
        }

        binding.tvWelcome.text = getString(if (isEdit) R.string.update_data else R.string.add_data)
        binding.btnAction.text = getString(if (isEdit) R.string.update else R.string.create)

        binding.btnAction.setOnClickListener {
            if (isEdit) updateCashflow() else saveCashflow()
        }

        binding.btnDate.setOnClickListener {
            showDatePicker()
        }

        observeViewModel()
    }

    private fun disableFormFields() {
        binding.clForm.children.forEach { it.isEnabled = false }
        binding.btnAction.isEnabled = false
    }

    private fun enableFormFields() {
        binding.clForm.children.forEach { it.isEnabled = true }
        binding.btnAction.isEnabled = true
    }

    private fun fetchCategoryAndEnableForm(type: Int) {
        disableFormFields()
        showLoading(true)
        viewModel.fetchCategoryByType(type)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoadingAndBlur(isLoading)
        }

        lifecycleScope.launch {
            viewModel.updateResult.collect { result ->
                result?.onSuccess {
                    showSuccessDialog("Data updated successfully")
                }?.onFailure {
                    showErrorDialog(it.message)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.addResult.collect { result ->
                result?.onSuccess {
                    showSuccessDialog("Data added successfully")
                }?.onFailure {
                    showErrorDialog(it.message)
                }
            }
        }

        viewModel.categoryData.observe(this) { categories ->
            if (categories.isNullOrEmpty()) {
                showErrorDialog("No categories available for the selected type.")
            } else {
                populateCategoryDropdown(categories)
                enableFormFields()
            }
        }
    }

    private fun populateCategoryDropdown(categories: List<CategoryItem>) {
        val categoryNames = categories.map { it.namaKategori.orEmpty() }
        val categoryMap = categories.associateBy { it.namaKategori.orEmpty() }

        val adapter = ArrayAdapter(this, R.layout.list_item, categoryNames)
        binding.autoCompleteTextView.setAdapter(adapter)

        binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedName = categoryNames[position]
            selectedCategoryId = categoryMap[selectedName]?.idKategori
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this, { _, year, monthOfYear, dayOfMonth ->
                cal.set(year, monthOfYear, dayOfMonth)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                binding.edtDate.setText(sdf.format(cal.time))
            }, year, month, day
        )
        dpd.show()
    }

    private fun validateFields(): Boolean {
        val description = binding.tiDescription.text.toString().trim()
        val nominal = binding.tiNominal.text.toString().trim()
        val date = binding.edtDate.text.toString().trim()

        return when {
            description.isEmpty() -> {
                binding.tiDescription.error = "Description cannot be empty"
                false
            }

            nominal.isEmpty() || nominal.toDoubleOrNull() == null -> {
                binding.tiNominal.error = "Nominal must be a valid number"
                false
            }

            date.isEmpty() -> {
                binding.edtDate.error = "Date cannot be empty"
                false
            }

            selectedCategoryId == null -> {
                showErrorDialog("Please select a category")
                false
            }

            id_type == null -> {
                showErrorDialog("Please select a transaction type")
                false
            }

            else -> true
        }
    }

    private fun saveCashflow() {
        if (!validateFields()) return
        val description = binding.tiDescription.text.toString().trim()
        val amount = binding.tiNominal.text.toString().toInt()
        val date = binding.edtDate.text.toString().trim()
        viewModel.addCashflow(id_type!!, selectedCategoryId!!, amount, date, description)
    }

    private fun updateCashflow() {
        if (!validateFields()) return
        val description = binding.tiDescription.text.toString().trim()
        val amount = binding.tiNominal.text.toString().toInt()
        val date = binding.edtDate.text.toString().trim()
        viewModel.updateCashflow(
            id_transaksi!!, id_type!!, selectedCategoryId!!, amount, date, description
        )
    }

    private fun showDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        onPositive: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveButtonText) { _, _ ->
                onPositive?.invoke()
            }
            create()
            show()
        }
    }

    private fun showSuccessDialog(message: String) {
        showDialog("Success", message = message, "OK") {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun showErrorDialog(message: String?) {
        showDialog("Error", message ?: "An Error Occurred", "Retry")
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showLoadingAndBlur(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.root.alpha = if (isLoading) 0.5f else 1f
    }
}
