package com.revolhope.data.feature.user.response

import com.google.gson.annotations.SerializedName

data class UserNetResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("lastLogin") val lastLogin: Long?
)
