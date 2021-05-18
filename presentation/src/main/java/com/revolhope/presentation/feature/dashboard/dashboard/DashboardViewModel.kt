package com.revolhope.presentation.feature.dashboard.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.domain.feature.user.usecase.FetchUserUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import com.revolhope.presentation.library.extensions.launchAsync
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val fetchUserUseCase: FetchUserUseCase
) : BaseViewModel() {

    private val _userLiveData = MutableLiveData<UserModel>()
    val userLiveData: LiveData<UserModel> get() = _userLiveData

    fun fetchUser() {
        launchAsync(
            asyncTask = fetchUserUseCase::invoke,
            onTaskCompleted = {
                handleState(
                    state = this,
                    onSuccess = _userLiveData::setValue
                )
            }
        )
    }
}
