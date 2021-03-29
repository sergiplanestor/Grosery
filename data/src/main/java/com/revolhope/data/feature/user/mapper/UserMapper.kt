package com.revolhope.data.feature.user.mapper

import com.revolhope.data.common.date.DateMapper
import com.revolhope.data.feature.user.response.UserResponse
import com.revolhope.domain.common.model.DateModel
import com.revolhope.domain.feature.user.model.UserModel

object UserMapper {

    fun fromUserResponseToModel(response: UserResponse): UserModel =
        UserModel(
            id = response.id ?: "",
            name = response.name ?: "",
            email = response.email ?: "",
            pwd = response.pwd,
            isRememberMe = response.isRememberMe == 1,
            lastLogin = response.lastLogin?.let(DateMapper::parseToModel) ?: DateModel.empty,
        )

}