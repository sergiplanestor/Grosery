package com.revolhope.data.feature.storage.local

import android.content.SharedPreferences
import com.revolhope.data.common.extensions.fromJSON
import com.revolhope.data.common.extensions.json
import com.revolhope.data.feature.user.datasource.UserLocalDataSource
import com.revolhope.data.feature.user.response.UserLocalResponse
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    UserLocalDataSource {

    private companion object {
        private const val USER = "grocery.prefs.user"
    }

    override suspend fun fetchUser(): UserLocalResponse? =
        sharedPreferences.getString(USER, null)?.fromJSON(UserLocalResponse::class)

    override suspend fun insertOrUpdateUser(user: UserLocalResponse) {
        sharedPreferences.edit().apply {
            putString(USER, user.json)
            apply()
        }
    }
}
