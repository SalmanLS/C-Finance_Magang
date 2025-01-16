package com.dicoding.c_finance.view.managehub

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.dicoding.c_finance.R
import com.dicoding.c_finance.databinding.FragmentCategoryDialogBinding
import com.dicoding.c_finance.model.response.category.CategoryItem
import com.dicoding.c_finance.view.managehub.CategoryFragment.Companion.TYPE_EXPENSE
import com.dicoding.c_finance.view.managehub.CategoryFragment.Companion.TYPE_INCOME

class ManageCategoryDialogFragment : DialogFragment() {
    private var _binding: FragmentCategoryDialogBinding? = null
    private val binding get() = _binding!!

    private var category: CategoryItem? = null

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: CategoryItem? = null): ManageCategoryDialogFragment {
            val fragment = ManageCategoryDialogFragment()
            val args = Bundle().apply { putParcelable(ARG_CATEGORY, category) }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentCategoryDialogBinding.inflate(layoutInflater)
        category = arguments?.getParcelable(ARG_CATEGORY)

        category?.let {
            binding.tiCategoryName.setText(it.namaKategori)
            if (it.idTipe == 1) {
                binding.radioGroupCategoryType.check(R.id.radioIncome)
            } else {
                binding.radioGroupCategoryType.check(R.id.radioExpense)
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton(if (category == null) "Save" else "Update") { _, _ ->
                handleSaveOrUpdate()
            }
            .setNeutralButton("Delete") { _, _ -> handleDelete() }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun handleSaveOrUpdate() {
        val name = binding.tiCategoryName.text.toString().trim()
        val idType = if (binding.radioIncome.isChecked) TYPE_INCOME else TYPE_EXPENSE
        if (name.isNotEmpty()) {
            val action = if (category == null) "SAVE" else "UPDATE"
            val result = Bundle().apply {
                putString("ACTION", action)
                putParcelable("CATEGORY", CategoryItem(category?.idKategori, idType, name))
            }
            parentFragmentManager.setFragmentResult("CATEGORY_RESULT", result)
            dismiss()
        }
    }


    private fun handleDelete() {
        category?.let {
            val result = Bundle().apply {
                putString("ACTION", "DELETE")
                putParcelable("CATEGORY", it)
            }
            parentFragmentManager.setFragmentResult("CATEGORY_RESULT", result)
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

