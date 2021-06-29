package com.revolhope.presentation.feature.dashboard.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.usecase.FetchUserUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val fetchUserUseCase: FetchUserUseCase
) : BaseViewModel() {

    private val _userLiveData = MutableLiveData<UserModel>()
    val userLiveData: LiveData<UserModel> get() = _userLiveData

    fun fetchUser() {
        invokeUseCase(
            useCase = fetchUserUseCase,
            request = FetchUserUseCase.RequestParams,
            loadingModel = null,
            onSuccessCollected = { it?.let(_userLiveData::setValue) }
            /* Default behavior for Loading and Failure states */
        )
    }
}
