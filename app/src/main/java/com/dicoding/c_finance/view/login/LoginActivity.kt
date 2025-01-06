package com.dicoding.c_finance.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.ActivityLoginBinding
import com.dicoding.c_finance.view.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupAction()
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginResult.collect { result ->
                loginViewModel.isLoading.observe(this@LoginActivity) { isLoading ->
                    showLoading(isLoading)
                }
                result?.onSuccess {
                    showSuccessDialog()
                }?.onFailure {
                    showErrorDialog(it.message)
                }
            }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Success")
            setMessage("Login Success!.")
            setPositiveButton("OK") { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
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

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val username = binding.tiUsername.text.toString()
            val password = binding.tiPassword.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                showErrorDialog("Check your credentials!")
            } else {
                loginViewModel.login(username, password)
            }
        }
    }

    private fun showErrorDialog(message: String?) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message ?: "An Error Occurred")
            setPositiveButton("Retry", null)
            create()
            show()
        }
    }
}