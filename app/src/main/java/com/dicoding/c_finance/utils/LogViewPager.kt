package com.dicoding.c_finance.utils

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.c_finance.view.log.RecycleBinFragment
import com.dicoding.c_finance.view.log.UserLogFragment
import com.dicoding.c_finance.view.managehub.CategoryFragment
import com.dicoding.c_finance.view.managehub.UserFragment

class LogViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = listOf(UserLogFragment(), RecycleBinFragment())

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
