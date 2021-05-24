package com.revolhope.domain.feature.authentication.model

import android.os.Parcelable
import com.revolhope.domain.common.model.DateModel
import com.revolhope.domain.feature.grocery.model.UserSharedModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val pwd: String?,
    val isRememberMe: Boolean,
    val lastLogin: DateModel
) : Parcelable {

    val sharedModel: UserSharedModel get() =
        UserSharedModel(
            id = id,
            username = name,
            email = email
        )
}
