package com.dicoding.c_finance.utils

import com.dicoding.c_finance.model.response.user.UsersItem

interface UserActionListener {
    fun onUserDeleted(userId: Int)
    fun onUserEdited(user: UsersItem)
}
