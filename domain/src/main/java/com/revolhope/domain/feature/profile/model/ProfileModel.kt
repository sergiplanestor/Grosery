package com.revolhope.domain.feature.profile.model

import android.os.Parcelable
import com.revolhope.domain.common.model.DateModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileModel(
    val userId: String,
    val email: String,
    val username: String,
    val avatar: ProfileAvatar,
    val lastContributionOn: DateModel,
    val lastConnectionOn: DateModel
): Parcelable
