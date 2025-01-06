package com.dicoding.c_finance.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.ActivityMainBinding
import com.dicoding.c_finance.view.cashflow.CashflowFragment
import com.dicoding.c_finance.view.home.HomeFragment
import com.dicoding.c_finance.view.log.LogFragment
import com.dicoding.c_finance.view.login.LoginActivity
import com.dicoding.c_finance.view.users.UsersFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        mainViewModel.getSession().observe(this) { token ->
            if (token?.token.isNullOrEmpty()) {
                navigateToLoginActivity()
            } else {
                Log.d("MainActivity", "Token: ${token?.token}")
            }
        }

        val bottomNavigationView = binding.bnView
        supportFragmentManager.beginTransaction().replace(R.id.flFragment, HomeFragment())
            .commit()

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, HomeFragment())
                        .commit()
                    true
                }

                R.id.nav_users -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, UsersFragment())
                        .commit()
                    true
                }

                R.id.nav_list -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, CashflowFragment())
                        .commit()
                    true
                }

                R.id.nav_log -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, LogFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}