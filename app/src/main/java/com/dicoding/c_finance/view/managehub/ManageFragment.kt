package com.dicoding.c_finance.view.managehub

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.FragmentManageBinding
import com.dicoding.c_finance.model.response.category.CategoryItem
import com.dicoding.c_finance.model.response.user.UsersItem
import com.dicoding.c_finance.utils.CategoryAdapter
import com.dicoding.c_finance.utils.UserAdapter
import com.dicoding.c_finance.view.managehub.viewmodel.CategoryViewModel
import com.dicoding.c_finance.view.managehub.viewmodel.UsersViewModel
import kotlinx.coroutines.launch

class ManageFragment : Fragment() {
    private lateinit var binding: FragmentManageBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val usersViewModel by viewModels<UsersViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val categoryViewModel by viewModels<CategoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val addUsersLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            usersViewModel.fetchUsers()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupRadioGroup()
        setupResultListener()
        binding.btnAddCategory.setOnClickListener {
            openAddDialog()
        }
    }

    private fun setupView() {
        userAdapter = UserAdapter { user ->
            showUserDetailDialog(user)
        }
        binding.rvUsers.adapter = userAdapter
        binding.rvUsers.layoutManager = LinearLayoutManager(context)

        categoryAdapter = CategoryAdapter { category -> openUpdateDialog(category) }
        binding.rvCategory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }

        if (usersViewModel.userData.value == null) {
            usersViewModel.fetchUsers()
        }
        usersViewModel.userData.observe(viewLifecycleOwner) { users ->
            userAdapter.submitList(users)
        }
        usersViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        categoryViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        binding.btnAddUser.setOnClickListener {
            goToAddUpdateUserActivity()
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

        categoryViewModel.selectedType.observe(viewLifecycleOwner) { selectedType ->
            categoryViewModel.fetchCategoryByType(selectedType)
        }
    }

    private fun showUserDetailDialog(user: UsersItem) {
        val dialog = ManageUserDialogFragment.newInstance(user)
        dialog.setOnEditListener { userToEdit ->
            val intent = Intent(requireContext(), ManageUsersAddUpdateActivity::class.java)
            intent.putExtra(USER_ID, userToEdit)
            addUsersLauncher.launch(intent)
        }
        dialog.setOnDeleteListener { userToDelete ->
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete ${userToDelete.username}?")
                .setPositiveButton("Yes") { _, _ ->
                    userToDelete.idUser?.let { usersViewModel.deleteUser(it.toInt()) }
                }
                .setNegativeButton("No", null)
                .show()
        }
        dialog.show(parentFragmentManager, "UserDetailDialog")
    }

    private fun goToAddUpdateUserActivity() {
        val intent = Intent(requireContext(), ManageUsersAddUpdateActivity::class.java)
        addUsersLauncher.launch(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressIndicator.visibility = View.VISIBLE
            binding.root.alpha = 0.5f
        } else {
            binding.progressIndicator.visibility = View.GONE
            binding.root.alpha = 1f
        }
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

    private fun openAddDialog() {
        val dialog = ManageCategoryDialogFragment.newInstance()
        dialog.show(parentFragmentManager, "AddCategoryDialog")
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

    companion object {
        const val USER_ID = "id_user"
        const val TYPE_INCOME = 1
        const val TYPE_EXPENSE = 2
    }
}