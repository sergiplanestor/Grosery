package com.revolhope.data.feature.profile.mapper

import com.revolhope.data.feature.profile.response.ProfileResponse
import com.revolhope.domain.common.model.DateModel
import com.revolhope.domain.common.model.asDateModelOrEmpty
import com.revolhope.domain.common.model.asDateModelOrToday
import com.revolhope.domain.feature.profile.model.ProfileAvatar
import com.revolhope.domain.feature.profile.model.ProfileModel

object ProfileMapper {

    fun fromProfileResponseToModel(response: ProfileResponse): ProfileModel =
        ProfileModel(
            userId = response.userId.orEmpty(),
            email = response.email.orEmpty(),
            username = response.username.orEmpty(),
            avatar = response.avatarId?.let(ProfileAvatar::fromId) ?: ProfileAvatar.NONE,
            lastContributionOn = response.lastContributionOn.asDateModelOrEmpty(),
            lastConnectionOn = response.lastContributionOn.asDateModelOrToday(),
        )

    fun fromProfileModelToResponse(model: ProfileModel): ProfileResponse =
        ProfileResponse(
            userId = model.userId,
            email = model.email,
            username = model.username,
            avatarId = model.avatar.id,
            lastContributionOn = model.lastContributionOn.value,
            lastConnectionOn = model.lastConnectionOn.value
        )

}
