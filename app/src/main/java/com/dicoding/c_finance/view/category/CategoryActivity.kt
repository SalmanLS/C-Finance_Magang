package com.dicoding.c_finance.view.category

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.ActivityCategoryBinding
import com.dicoding.c_finance.model.response.category.CategoryItem
import com.dicoding.c_finance.utils.CategoryAdapter
import com.dicoding.c_finance.view.category.viewmodel.CategoryViewModel

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private val categoryViewModel by viewModels<CategoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var categoryAdapter: CategoryAdapter

    companion object {
        private const val ID_TYPE_INCOME = 1
        private const val ID_TYPE_EXPENSE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupRecyclerView()
        setupRadioGroup()
        setupAddButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            openDetailDialog(category)
        }
        binding.rvCategory.apply {
            layoutManager = LinearLayoutManager(this@CategoryActivity)
            adapter = categoryAdapter
        }
    }

    private fun setupRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val idType = when (checkedId) {
                R.id.radioIncome -> ID_TYPE_INCOME
                R.id.radioExpense -> ID_TYPE_EXPENSE
                else -> return@setOnCheckedChangeListener
            }
            fetchCategoryByType(idType)
        }
        binding.radioGroup.check(R.id.radioIncome)
    }

    private fun setupAddButton() {
        binding.btnAddCategory.setOnClickListener {
            openAddDialog()
        }
    }

    private fun fetchCategoryByType(idType: Int) {
        categoryViewModel.fetchCategoryByType(idType)
    }

    private fun observeViewModel() {
        categoryViewModel.isLoading.observe(this) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        categoryViewModel.categoryData.observe(this) { categories ->
            if (!categories.isNullOrEmpty()) {
                categoryAdapter.submitList(categories)
                showRecyclerView()
            } else {
                categoryAdapter.submitList(emptyList())
                showEmptyState()
            }
        }
    }
    private fun openAddDialog() {
        val dialog = CategoryDialogFragment.newInstance()
        dialog.setOnSaveListener { newCategory ->
            newCategory.namaKategori?.let { nama ->
                newCategory.idTipe?.let { idType ->
                    categoryViewModel.addCategory(nama, idType)
                }
            }
        }
        dialog.show(supportFragmentManager, "AddCategoryDialog")
    }
    private fun openDetailDialog(category: CategoryItem) {
        val dialog = CategoryDialogFragment.newInstance(category)
        dialog.setOnSaveListener { updatedCategory ->
            updatedCategory.idKategori?.let { id ->
                updatedCategory.namaKategori?.let { nama ->
                    updatedCategory.idTipe?.let { idType ->
                        // Update category
                        categoryViewModel.updateCategory(id, nama, idType)

                        // Fetch categories for the current type
                        val currentIdType = when (binding.radioGroup.checkedRadioButtonId) {
                            R.id.radioIncome -> ID_TYPE_INCOME
                            R.id.radioExpense -> ID_TYPE_EXPENSE
                            else -> return@setOnSaveListener
                        }
                        fetchCategoryByType(currentIdType)
                    }
                }
            }
        }
        dialog.setOnDeleteListener { categoryToDelete ->
            categoryToDelete.idKategori?.let { id ->
                categoryViewModel.deleteCategory(id)

                // Fetch categories for the current type
                val currentIdType = when (binding.radioGroup.checkedRadioButtonId) {
                    R.id.radioIncome -> ID_TYPE_INCOME
                    R.id.radioExpense -> ID_TYPE_EXPENSE
                    else -> return@setOnDeleteListener
                }
                fetchCategoryByType(currentIdType)
            }
        }
        dialog.show(supportFragmentManager, "EditCategoryDialog")
    }


    private fun showRecyclerView() {
        binding.rvCategory.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.rvCategory.visibility = View.GONE
    }
}
