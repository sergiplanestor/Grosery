package com.revolhope.data.feature.storage.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.revolhope.data.feature.user.datasource.UserLocalDataSource
import com.revolhope.data.feature.user.response.UserLocalResponse
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    UserLocalDataSource {

    private companion object {
        private const val USER = "grocery.prefs.user"
    }

    override suspend fun fetchUser(): UserLocalResponse? =
        sharedPreferences.getString(USER, null)?.let {
            Gson().fromJson(it, UserLocalResponse::class.java)
        }

    override suspend fun insertOrUpdateUser(user: UserLocalResponse) {
        sharedPreferences.edit().apply {
            putString(USER, Gson().toJson(user))
            apply()
        }
    }
}
