package com.dicoding.c_finance.view.log

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.c_finance.R
import com.dicoding.c_finance.ViewModelFactory
import com.dicoding.c_finance.databinding.FragmentRecycleBinBinding
import com.dicoding.c_finance.utils.RecyclebinAdapter
import com.dicoding.c_finance.view.log.viewmodel.RecyclebinViewModel

class RecycleBinFragment : Fragment() {

    private var _binding: FragmentRecycleBinBinding? = null
    private val binding get() = _binding!!

    private lateinit var recycleBinAdapter: RecyclebinAdapter
    private val viewModel by viewModels<RecyclebinViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecycleBinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        recycleBinAdapter = RecyclebinAdapter()
        binding.rvRcBin.adapter = recycleBinAdapter
        binding.rvRcBin.layoutManager = LinearLayoutManager(requireContext())

        if (viewModel.recycleData.value == null) {
            viewModel.fetchRecyclebin()
        }

        viewModel.recycleData.observe(viewLifecycleOwner) {
            recycleBinAdapter.submitList(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.root.alpha = if (isLoading) 0.5f else 1.0f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
