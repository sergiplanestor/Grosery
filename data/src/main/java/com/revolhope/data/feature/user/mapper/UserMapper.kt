package com.revolhope.data.feature.user.mapper

import com.revolhope.data.common.date.DateMapper
import com.revolhope.data.feature.user.response.UserLocalResponse
import com.revolhope.domain.common.model.DateModel
import com.revolhope.domain.feature.user.model.UserModel

object UserMapper {

    fun fromUserResponseToModel(response: UserLocalResponse): UserModel =
        UserModel(
            id = response.id ?: "",
            name = response.name ?: "",
            email = response.email ?: "",
            pwd = response.pwd,
            isRememberMe = response.isRememberMe == 1,
            lastLogin = response.lastLogin?.let(DateMapper::parseToModel) ?: DateModel.empty,
        )

    fun fromUserModelToResponse(model: UserModel): UserLocalResponse =
        UserLocalResponse(
            id = model.id,
            name = model.name,
            email = model.email,
            pwd = model.pwd,
            isRememberMe = if (model.isRememberMe) 1 else 0,
            lastLogin = model.lastLogin.value
        )
    
}
