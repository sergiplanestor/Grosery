package com.revolhope.domain.feature.user.model

import com.revolhope.domain.common.model.DateModel

data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val pwd: String?,
    val isRememberMe: Boolean,
    val lastLogin: DateModel
)
