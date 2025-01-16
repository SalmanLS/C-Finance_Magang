package com.dicoding.c_finance.view.managehub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicoding.c_finance.databinding.FragmentManageBinding
import com.dicoding.c_finance.utils.ManageViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class ManageFragment : Fragment() {
    private lateinit var binding: FragmentManageBinding
    private lateinit var adapter: ManageViewPagerAdapter

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
    }

    private fun setupView() {
        adapter = ManageViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Users"
                1 -> "Categories"
                else -> "Tab $position"
            }
        }.attach()
    }
}