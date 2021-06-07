package com.revolhope.presentation.feature.dashboard.profile.edit.avatar.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.profile.model.ProfileAvatar
import com.revolhope.domain.feature.profile.model.ProfileModel
import com.revolhope.presentation.library.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditAvatarViewModel @Inject constructor() : BaseViewModel() {

    private var currentAvatar: Int = 0

    private val _avatarsLiveData = MutableLiveData<List<ProfileAvatar>>()
    val avatarsLiveData: LiveData<List<ProfileAvatar>> get() = _avatarsLiveData

    fun fetchAvatars() {
        _avatarsLiveData.value = ProfileAvatar.values().filter { it != ProfileAvatar.NONE }.toList()
    }

    fun saveChanges(profileModel: ProfileModel, avatar: ProfileAvatar) {

    }
}
