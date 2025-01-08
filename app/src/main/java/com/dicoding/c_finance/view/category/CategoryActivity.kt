package com.dicoding.c_finance.view.category

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.ActivityCategoryBinding
import com.dicoding.c_finance.model.response.category.CategoryItem
import com.dicoding.c_finance.utils.CategoryAdapter
import com.dicoding.c_finance.view.category.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private val categoryViewModel by viewModels<CategoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var categoryAdapter: CategoryAdapter

    companion object {
        const val TYPE_INCOME = 1
        const val TYPE_EXPENSE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupResultListener()
        setupRecyclerView()
        setupRadioGroup()
        setupAddButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category -> openUpdateDialog(category) }
        binding.rvCategory.apply {
            layoutManager = LinearLayoutManager(this@CategoryActivity)
            adapter = categoryAdapter
        }
    }

    private fun setupRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedType = when (checkedId) {
                R.id.radioIncome -> TYPE_INCOME
                R.id.radioExpense -> TYPE_EXPENSE
                else -> return@setOnCheckedChangeListener
            }
            categoryViewModel.setSelectedType(selectedType)
        }
        binding.radioGroup.check(R.id.radioIncome)
    }

    private fun setupAddButton() {
        binding.btnAddCategory.setOnClickListener { openAddDialog() }
    }

    private fun observeViewModel() {
        categoryViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        categoryViewModel.isLoading2.observe(this) { isLoading ->
            showLoading2(isLoading)
        }
        lifecycleScope.launch {
            categoryViewModel.categoryResult.collect{ result ->
                result?.onSuccess {
                    showSuccessDialog("A change has been made!")
                }?.onFailure {
                    showErrorDialog(it.message)
                }
            }
        }

        categoryViewModel.categoryData.observe(this) { categories ->
            categoryAdapter.submitList(categories)
            binding.rvCategory.visibility =
                if (!categories.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        categoryViewModel.selectedType.observe(this) { selectedType ->
            categoryViewModel.fetchCategoryByType(selectedType)
        }
    }

    private fun openAddDialog() {
        val dialog = CategoryDialogFragment.newInstance()
        dialog.show(supportFragmentManager, "AddCategoryDialog")
    }

    private fun openUpdateDialog(category: CategoryItem) {
        val dialog = CategoryDialogFragment.newInstance(category)
        dialog.show(supportFragmentManager, "EditCategoryDialog")
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
            categoryViewModel.fetchCategoryByType(categoryViewModel.selectedType.value ?: 0)
        }
    }

    private fun showErrorDialog(message: String?) {
        showDialog("Error", message ?: "An Error Occurred", "Retry")
    }

    private fun showLoading2(isLoading: Boolean) {
        binding.progressIndicator2.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.root.alpha = if (isLoading) 0.5f else 1f
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupResultListener() {
        supportFragmentManager.setFragmentResultListener("CATEGORY_RESULT", this) { _, bundle ->
            val action = bundle.getString("ACTION")
            val category = bundle.getParcelable<CategoryItem>("CATEGORY")

            when (action) {
                "SAVE" -> category?.let {
                    categoryViewModel.addCategory(it.namaKategori ?: "", it.idTipe ?: 0)
                    categoryViewModel.setSelectedType(it.idTipe ?: 0)
                    binding.radioGroup.check(
                        if (it.idTipe == TYPE_INCOME) R.id.radioIncome else R.id.radioExpense
                    )
                }

                "UPDATE" -> category?.let {
                    categoryViewModel.updateCategory(
                        it.idKategori ?: 0,
                        it.namaKategori ?: "",
                        it.idTipe ?: 0
                    )
                    categoryViewModel.setSelectedType(it.idTipe ?: 0)
                    binding.radioGroup.check(
                        if (it.idTipe == TYPE_INCOME) R.id.radioIncome else R.id.radioExpense
                    )
                }

                "DELETE" -> category?.let {
                    categoryViewModel.deleteCategory(it.idKategori ?: 0)
                    binding.radioGroup.check(
                        if (it.idTipe == TYPE_INCOME) R.id.radioIncome else R.id.radioExpense
                    )
                }
            }
        }
    }

}

