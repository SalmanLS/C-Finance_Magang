package com.dicoding.c_finance.view.recycle

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.ActivityRecycleBinBinding
import com.dicoding.c_finance.utils.RecyclebinAdapter
import com.dicoding.c_finance.view.recycle.viewmodel.RecyclebinViewModel

class RecycleBinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecycleBinBinding
    private lateinit var recycleBinAdapter: RecyclebinAdapter
    private val viewModel by viewModels<RecyclebinViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupView()
    }

    private fun setupView() {
        recycleBinAdapter = RecyclebinAdapter()
        binding.rvRcBin.adapter = recycleBinAdapter
        binding.rvRcBin.layoutManager = LinearLayoutManager(this)

        if (viewModel.recycleData.value == null) {
            viewModel.fetchRecyclebin()
        }

        viewModel.recycleData.observe(this, {
            recycleBinAdapter.submitList(it)
        })

        viewModel.isLoading.observe(this, {
            showLoading(it)
        })
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
}