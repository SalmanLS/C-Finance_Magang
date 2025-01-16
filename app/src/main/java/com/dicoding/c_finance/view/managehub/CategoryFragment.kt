package com.dicoding.c_finance.view.managehub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.FragmentCategoryBinding
import com.dicoding.c_finance.model.response.category.CategoryItem
import com.dicoding.c_finance.utils.CategoryAdapter
import com.dicoding.c_finance.view.managehub.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private val categoryViewModel by viewModels<CategoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupRadioGroup()
        setupResultListener()
    }

    private fun setupView() {
        categoryAdapter = CategoryAdapter { category -> openUpdateDialog(category) }
        binding.rvCategory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }
        categoryViewModel.selectedType.observe(viewLifecycleOwner) { selectedType ->
            categoryViewModel.fetchCategoryByType(selectedType)
        }
        lifecycleScope.launch {
            categoryViewModel.categoryResult.collect { result ->
                result?.onSuccess {
                    showSuccessDialog("A change has been made!")
                }?.onFailure {
                    showErrorDialog(it.message)
                }
            }
        }
        categoryViewModel.categoryData.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
            binding.rvCategory.visibility =
                if (!categories.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        categoryViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        binding.btnAddCategory.setOnClickListener {
            openAddDialog()
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.root.alpha = if (isLoading) 0.5f else 1.0f
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

    private fun showDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        onPositive: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(requireContext()).apply {
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

    private fun openUpdateDialog(category: CategoryItem) {
        val dialog = ManageCategoryDialogFragment.newInstance(category)
        dialog.show(parentFragmentManager, "EditCategoryDialog")
    }

    private fun setupResultListener() {
        parentFragmentManager.setFragmentResultListener("CATEGORY_RESULT", this) { _, bundle ->
            val action = bundle.getString("ACTION")
            val category = bundle.getParcelable<CategoryItem>("CATEGORY")
            when (action) {
                "SAVE" -> category?.let {
                    categoryViewModel.addCategory(it.namaKategori ?: "", it.idTipe ?: 0)
                }
                "UPDATE" -> category?.let {
                    categoryViewModel.updateCategory(
                        it.idKategori ?: 0, it.namaKategori ?: "", it.idTipe ?: 0
                    )
                }
                "DELETE" -> category?.let {
                    categoryViewModel.deleteCategory(it.idKategori ?: 0)
                }
            }
        }
    }

    private fun openAddDialog() {
        val dialog = ManageCategoryDialogFragment.newInstance()
        dialog.show(parentFragmentManager, "AddCategoryDialog")
    }


    companion object {
        const val TYPE_INCOME = 1
        const val TYPE_EXPENSE = 2
    }
}
