package com.dicoding.c_finance.model.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class UserPreference private constructor(private val datastore: DataStore<Preferences>) {
    suspend fun saveToken(userToken: UserToken) {
        datastore.edit { preferences ->
            preferences[TOKEN_KEY] = userToken.token
            preferences[USERNAME_KEY] = userToken.username
        }
    }

    fun getToken(): Flow<UserToken> {
        return datastore.data.map { preferences ->
            UserToken(
                preferences[USERNAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun clearToken() {
        datastore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USERNAME_KEY = stringPreferencesKey("username")

        fun getInstance(context: Context): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(context.dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}