package com.revolhope.data.feature.user.datasource

import com.revolhope.domain.feature.authentication.model.UserModel

object UserCacheDataSource {

    private var _user: UserModel? = null
    val user: UserModel? = _user

    val isUserCached: Boolean get() = _user != null

    fun insert(user: UserModel) {
        this._user = user
    }

    fun clear() {
        this._user = null
    }
}
