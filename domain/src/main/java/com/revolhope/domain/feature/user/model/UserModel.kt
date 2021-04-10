package com.revolhope.domain.feature.user.model

import android.os.Parcelable
import com.revolhope.domain.common.model.DateModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val pwd: String?,
    val isRememberMe: Boolean,
    val lastLogin: DateModel
) : Parcelable
