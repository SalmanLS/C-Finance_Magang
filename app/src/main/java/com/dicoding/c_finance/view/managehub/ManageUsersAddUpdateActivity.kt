package com.dicoding.c_finance.view.managehub

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.ActivityUsersAddUpdateBinding
import com.dicoding.c_finance.model.response.user.UsersItem
import com.dicoding.c_finance.utils.UserInputFields
import com.dicoding.c_finance.view.managehub.viewmodel.UsersAddUpdateViewModel
import kotlinx.coroutines.launch

class ManageUsersAddUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersAddUpdateBinding
    private val viewModel by viewModels<UsersAddUpdateViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userId: Int? = null
    private var id_role: Int? = null
    private var isEdit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val userItem: UsersItem? = intent.getParcelableExtra(UserFragment.USER_ID)

        isEdit = userItem != null

        if (isEdit) {
            userId = userItem?.idUser.toString().toInt()
            binding.tiUsername.setText(userItem?.username)
            binding.tfPassword.hint = getString(R.string.newpassword)
            binding.tiPhone.setText(userItem?.noHp)
            binding.tiName.setText(userItem?.nama)
            id_role = userItem?.idRole.toString().toInt()

            val radioButton = binding.radioGroup.getChildAt(id_role!! - 1) as? RadioButton
            radioButton?.isChecked = true
        }


        binding.tvWelcome.text = getString(if (isEdit) R.string.update_user else R.string.add_user)
        binding.btnAction.text = getString(if (isEdit) R.string.update else R.string.create)

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            id_role = radioButton?.let { group.indexOfChild(it) + 1 }
        }

        binding.btnAction.setOnClickListener {
            if (isEdit) updateUser() else saveUser()
        }

        observeViewModel()
    }


    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        lifecycleScope.launch {
            viewModel.updateResult.collect { result ->
                result?.onSuccess {
                    showSuccessDialog("User updated successfully")
                }?.onFailure {
                    showErrorDialog(it.message)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.addResult.collect { result ->
                result?.onSuccess {
                    showSuccessDialog("User added successfully")
                }?.onFailure {
                    showErrorDialog(it.message)
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        val inputFields = getInputFields()

        when {
            inputFields.username.isEmpty() -> {
                binding.tiUsername.error = "Username cannot be empty"
                return false
            }

            inputFields.password.isEmpty() -> {
                binding.tiPassword.error = "Password cannot be empty"
                return false
            }

            inputFields.phone.isEmpty() -> {
                binding.tiPhone.error = "Phone number cannot be empty"
                return false
            }

            inputFields.nama.isEmpty() -> {
                binding.tiName.error = "Nama cannot be empty"
                return false
            }

            id_role == null -> {
                showErrorDialog("Please select a role")
                return false
            }
        }
        return true
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
        binding.root.alpha = if (isLoading) 0.5f else 1f
    }

    private fun getInputFields(): UserInputFields {
        val username = binding.tiUsername.text.toString().trim()
        val password = binding.tiPassword.text.toString().trim()
        val phone = binding.tiPhone.text.toString().trim()
        val nama = binding.tiName.text.toString().trim() // Assume `tiNama` is added in XML
        return UserInputFields(username, password, phone, nama)
    }


    private fun saveUser() {
        if (!validateFields()) return
        val inputFields = getInputFields()
        viewModel.addUser(
            inputFields.nama,
            inputFields.username,
            inputFields.password,
            inputFields.phone,
            id_role!!
        )
    }

    private fun updateUser() {
        if (!validateFields()) return
        val inputFields = getInputFields()
        viewModel.updateUser(
            userId!!,
            inputFields.nama,
            inputFields.username,
            inputFields.password,
            inputFields.phone,
            id_role!!
        )
    }

}
