package com.revolhope.data.feature.profile.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("user_id") val userId: String?,
    @SerializedName("email")val email: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("avatar") val avatarId: Int?,
    @SerializedName("last_contribution_on") val lastContributionOn: Long?,
    @SerializedName("last_connection_on") val lastConnectionOn: Long?
)
