package com.dicoding.c_finance.view.category

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.dicoding.c_finance.R
import com.dicoding.c_finance.databinding.FragmentCategoryDialogBinding
import com.dicoding.c_finance.model.response.category.CategoryItem

class CategoryDialogFragment : DialogFragment() {
    private var _binding: FragmentCategoryDialogBinding? = null
    private val binding get() = _binding!!

    private var onSaveListener: ((CategoryItem) -> Unit)? = null
    private var onDeleteListener: ((CategoryItem) -> Unit)? = null

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: CategoryItem? = null): CategoryDialogFragment {
            val fragment = CategoryDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentCategoryDialogBinding.inflate(layoutInflater)
        val category = arguments?.getParcelable<CategoryItem>(ARG_CATEGORY)

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
                val name = binding.tiCategoryName.text.toString().trim()
                val idType = if (binding.radioIncome.isChecked) 1 else 2
                if (name.isNotEmpty()) {
                    onSaveListener?.invoke(CategoryItem(category?.idKategori, idType, name))
                }
            }
            .apply {
                if (category != null) {
                    setNeutralButton("Delete") { _, _ ->
                        onDeleteListener?.invoke(category)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnSaveListener(listener: (CategoryItem) -> Unit) {
        onSaveListener = listener
    }

    fun setOnDeleteListener(listener: (CategoryItem) -> Unit) {
        onDeleteListener = listener
    }
}
