package com.dicoding.c_finance.view.users

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.FragmentUsersBinding
import com.dicoding.c_finance.model.response.user.UsersItem
import com.dicoding.c_finance.utils.UserActionListener
import com.dicoding.c_finance.utils.UserAdapter
import com.dicoding.c_finance.view.users.viewmodel.UsersViewModel

class UsersFragment : Fragment() {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var userAdapter: UserAdapter
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        userAdapter = UserAdapter { user ->
            showUserDetailDialog(user)
        }
        binding.rvUsers.adapter = userAdapter
        binding.rvUsers.layoutManager = LinearLayoutManager(context)

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
    }

    private fun showUserDetailDialog(user: UsersItem) {
        val dialog = UserDetailDialogFragment.newInstance(user)
        dialog.setOnEditListener { userToEdit ->
            val intent = Intent(requireContext(), UsersAddUpdateActivity::class.java)
            intent.putExtra(USER_ID, userToEdit)
            startActivity(intent)
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
        val intent = Intent(requireContext(), UsersAddUpdateActivity::class.java)
        addUsersLauncher.launch(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading){
            binding.progressIndicator.visibility = View.VISIBLE
        } else {
            binding.progressIndicator.visibility = View.GONE
        }
    }

    companion object {
        const val USER_ID = "id_user"
    }
}