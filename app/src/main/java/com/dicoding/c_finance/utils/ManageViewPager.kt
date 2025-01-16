package com.dicoding.c_finance.utils

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.c_finance.view.managehub.CategoryFragment
import com.dicoding.c_finance.view.managehub.UserFragment

class ManageViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = listOf(UserFragment(), CategoryFragment())

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
