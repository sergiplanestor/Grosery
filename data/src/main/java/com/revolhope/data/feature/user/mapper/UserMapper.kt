package com.revolhope.data.feature.user.mapper

import com.revolhope.data.common.date.DateMapper
import com.revolhope.data.feature.user.response.UserLocalResponse
import com.revolhope.data.feature.user.response.UserNetResponse
import com.revolhope.domain.common.model.DateModel
import com.revolhope.domain.feature.user.model.UserModel

object UserMapper {

    fun fromUserLocalResponseToModel(response: UserLocalResponse): UserModel =
        UserModel(
            id = response.id.orEmpty(),
            name = response.name.orEmpty(),
            email = response.email.orEmpty(),
            pwd = response.pwd,
            isRememberMe = response.isRememberMe == 1,
            lastLogin = response.lastLogin?.let(DateMapper::parseToModel) ?: DateModel.empty
        )

    fun fromUserModelToLocalResponse(model: UserModel): UserLocalResponse =
        UserLocalResponse(
            id = model.id,
            name = model.name,
            email = model.email,
            pwd = model.pwd,
            isRememberMe = if (model.isRememberMe) 1 else 0,
            lastLogin = model.lastLogin.value
        )

    fun fromUserModelToNetResponse(model: UserModel): UserNetResponse =
        UserNetResponse(
            id = model.id,
            name = model.name,
            email = model.email,
            lastLogin = model.lastLogin.value
        )

    fun fromUserNetResponseToModel(response: UserNetResponse, pwd: String, isRememberMe: Boolean): UserModel =
        UserModel(
            id = response.id.orEmpty(),
            name = response.name.orEmpty(),
            email = response.email.orEmpty(),
            pwd = pwd,
            isRememberMe = isRememberMe,
            lastLogin = response.lastLogin?.let(DateMapper::parseToModel) ?: DateModel.empty
        )
}
