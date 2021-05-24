package com.revolhope.presentation.library.component.profilecard

import com.revolhope.domain.common.model.DateModel
import com.revolhope.domain.feature.profile.model.ProfileAvatar

data class ProfileCardUiModel(
    val avatar: ProfileAvatar,
    val username: String,
    val email: String,
    val numberOfLists: Int,
    val lastConnectedOn: DateModel,
    val lastUpdatedOn: DateModel,
    val onEditClick: () -> Unit
)
