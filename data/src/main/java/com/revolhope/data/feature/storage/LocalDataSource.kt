package com.revolhope.data.feature.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.revolhope.data.R
import com.revolhope.data.feature.user.response.UserResponse
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private companion object {
        private const val PREFS = "grocery.prefs"
        private const val USER = "grocery.prefs.user"
    }

    fun fetchUser(): UserResponse? =
        sharedPreferences.getString(USER, null)?.let {
            Gson().fromJson(it, UserResponse::class.java)
        }

    fun insertOrUpdateUser(user: UserResponse) {
        sharedPreferences.edit().apply {
            putString(USER, Gson().toJson(user))
            apply()
        }
    }

}