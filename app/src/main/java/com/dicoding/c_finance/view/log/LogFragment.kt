package com.dicoding.c_finance.view.log


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.dicoding.c_finance.R
import com.dicoding.c_finance.databinding.FragmentLogBinding
import com.dicoding.c_finance.utils.LogViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class LogFragment : Fragment() {
    private lateinit var binding: FragmentLogBinding
    private lateinit var adapter: LogViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    private fun setupView() {
        adapter = LogViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "User Log"
                1 -> "Recycle Bin"
                else -> "Tab $position"
            }
        }.attach()
    }
}
