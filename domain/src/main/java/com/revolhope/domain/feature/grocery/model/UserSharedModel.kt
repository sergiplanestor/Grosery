package com.revolhope.domain.feature.grocery.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserSharedModel(
    val id: String,
    val username: String,
    val email: String?
): Parcelable {

    companion object {
        val empty: UserSharedModel get() =
            UserSharedModel(
                id = "",
                username = "",
                email = ""
            )
    }

}
