package com.dicoding.c_finance.view.log

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.FragmentLogBinding
import com.dicoding.c_finance.utils.LoadingStateAdapter
import com.dicoding.c_finance.utils.PagingLogAdapter
import com.dicoding.c_finance.view.log.viewmodel.LogViewModel
import com.dicoding.c_finance.view.recycle.RecycleBinActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class LogFragment : Fragment() {
    private lateinit var binding: FragmentLogBinding
    private val viewModel by viewModels<LogViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var pagingLogAdapter: PagingLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.btnRcBin.setOnClickListener {
            val intent = Intent(requireContext(), RecycleBinActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        pagingLogAdapter = PagingLogAdapter { logId ->
            showDeleteConfirmationDialog(logId)
        }

        binding.rvLog.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pagingLogAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { pagingLogAdapter.retry() }
            )
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        lifecycleScope.launch {
            viewModel.logResult.collect { result ->
                result?.onSuccess {
                    showSuccessDialog("Log Deleted")
                    refreshLogs()
                }?.onFailure { error ->
                    showErrorDialog(error.message)
                }
            }
        }

        viewModel.pagingLog.observe(viewLifecycleOwner) {
            pagingLogAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun refreshLogs() {
        pagingLogAdapter.refresh()
    }

    private fun showDeleteConfirmationDialog(logId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this log?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteLog(logId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvLog.alpha = if (isLoading) 0.5f else 1f
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Success")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    private fun showErrorDialog(message: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message ?: "An error occurred")
            .setPositiveButton("Retry") { _, _ -> }
            .show()
    }
}
