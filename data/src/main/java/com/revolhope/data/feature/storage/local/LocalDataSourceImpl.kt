package com.revolhope.data.feature.storage.local

import android.content.SharedPreferences
import com.revolhope.data.common.extensions.asJson
import com.revolhope.data.common.extensions.fromJsonTo
import com.revolhope.data.feature.profile.datasource.ProfileLocalDataSource
import com.revolhope.data.feature.profile.response.ProfileResponse
import com.revolhope.data.feature.user.datasource.UserLocalDataSource
import com.revolhope.data.feature.user.response.UserLocalResponse
import javax.inject.Inject
import kotlin.reflect.KClass

class LocalDataSourceImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    UserLocalDataSource, ProfileLocalDataSource {

    private companion object {
        // User constants
        private const val USER = "grocery.prefs.user"
        // Profile constants
        private const val PROFILE = "grocery.prefs.profile"
    }

    // UserLocalDataSource Impl --------------------------------------------------------------------
    override suspend fun fetchUser(): UserLocalResponse? =
        fetchFromJson(USER, UserLocalResponse::class)

    override suspend fun insertOrUpdateUser(user: UserLocalResponse) {
        persistToJson(USER, user)
    }

    // ProfileLocalDataSource Impl -----------------------------------------------------------------
    override suspend fun fetchProfile(): ProfileResponse? =
        fetchFromJson(PROFILE, ProfileResponse::class)

    override suspend fun insertOrUpdateProfile(profile: ProfileResponse) {
        persistToJson(PROFILE, profile)
    }

    // Private methods -----------------------------------------------------------------------------
    private fun <T : Any> fetchFromJson(key: String, kClazz: KClass<T>): T? =
        sharedPreferences.getString(key, null)?.fromJsonTo(kClazz)

    private inline fun <reified T : Any> persistToJson(key: String, data: T) {
        sharedPreferences.edit().apply {
            putString(key, data.asJson())
            apply()
        }
    }
}
