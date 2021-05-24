package com.revolhope.domain.feature.profile.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileModel(
    val avatar: ProfileAvatar
): Parcelable
