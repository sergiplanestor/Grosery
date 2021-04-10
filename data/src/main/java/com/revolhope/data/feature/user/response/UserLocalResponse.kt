package com.revolhope.data.feature.user.response

import com.google.gson.annotations.SerializedName

data class UserLocalResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("pwd") val pwd: String?,
    @SerializedName("isRememberMe") val isRememberMe: Int?,
    @SerializedName("lastLogin") val lastLogin: Long?
)
