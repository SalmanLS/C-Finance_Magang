package com.dicoding.c_finance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.c_finance.model.repo.FinanceRepository
import com.dicoding.c_finance.utils.Injection
import com.dicoding.c_finance.view.login.LoginViewModel
import com.dicoding.c_finance.view.main.MainViewModel
import com.dicoding.c_finance.view.users.viewmodel.UsersAddUpdateViewModel
import com.dicoding.c_finance.view.users.viewmodel.UsersViewModel

class ViewModelFactory(private val financeRepository: FinanceRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(financeRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(financeRepository) as T
            }

            modelClass.isAssignableFrom(UsersViewModel::class.java) -> {
                UsersViewModel(financeRepository) as T
            }

            modelClass.isAssignableFrom(UsersAddUpdateViewModel::class.java) -> {
                UsersAddUpdateViewModel(financeRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}