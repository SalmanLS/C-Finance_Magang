package com.dicoding.c_finance.view.managehub

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.FragmentUserBinding
import com.dicoding.c_finance.model.response.user.UsersItem
import com.dicoding.c_finance.utils.UserAdapter
import com.dicoding.c_finance.view.managehub.viewmodel.UsersViewModel

class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding
    private val usersViewModel by viewModels<UsersViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val addUsersLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            usersViewModel.fetchUsers()
        }
    }
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        userAdapter = UserAdapter { user -> showUserDetailDialog(user) }
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
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
        binding.btnAddUser.setOnClickListener {
            goToAddUpdateUserActivity()
        }
        setupSearch()
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener {
            val query = it.toString()
            userAdapter.filter.filter(query)
        }
    }

    private fun goToAddUpdateUserActivity() {
        val intent = Intent(requireContext(), ManageUsersAddUpdateActivity::class.java)
        addUsersLauncher.launch(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.root.alpha = if (isLoading) 0.5f else 1.0f
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

    companion object {
        const val USER_ID = "id_user"
    }
}
