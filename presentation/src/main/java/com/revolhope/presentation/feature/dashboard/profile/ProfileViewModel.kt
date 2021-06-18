package com.revolhope.presentation.feature.dashboard.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.usecase.FetchUserUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fetchUserUseCase: FetchUserUseCase
) : BaseViewModel() {

    private val _userLiveData = MutableLiveData<UserModel>()
    val userLiveData: LiveData<UserModel> get() = _userLiveData

    fun fetchUser() {
        collectFlow(
            flowTask = fetchUserUseCase::invoke,
            onSuccessCollected = { it?.let(_userLiveData::setValue) }
        )
    }
}
