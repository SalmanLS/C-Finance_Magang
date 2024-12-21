package com.dicoding.c_finance.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.c_finance.R
import com.dicoding.c_finance.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val bottomNavigationView = binding.bnView

        supportFragmentManager.beginTransaction().replace(R.id.flFragment, HomeFragment())
            .commit()

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.flFragment, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_users -> {
                    supportFragmentManager.beginTransaction().replace(R.id.flFragment, UsersFragment())
                        .commit()
                    true
                }
                R.id.nav_list -> {
                    supportFragmentManager.beginTransaction().replace(R.id.flFragment, CashflowFragment())
                        .commit()
                    true
                }
                R.id.nav_log -> {
                    supportFragmentManager.beginTransaction().replace(R.id.flFragment, LogFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}