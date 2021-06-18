package com.revolhope.presentation.feature.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.request.LoginRequest
import com.revolhope.domain.feature.authentication.usecase.DoLoginUseCase
import com.revolhope.domain.feature.authentication.usecase.FetchUserUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fetchUserUseCase: FetchUserUseCase,
    private val doLoginUseCase: DoLoginUseCase
) : BaseViewModel() {

    val redirectToLoginLiveData: LiveData<UserModel?> get() = _redirectToLoginLiveData
    private val _redirectToLoginLiveData = MutableLiveData<UserModel?>()

    val onLoginResponseLiveData: LiveData<Boolean> get() = _onLoginResponseLiveData
    private val _onLoginResponseLiveData = MutableLiveData<Boolean>()

    val user: UserModel? get() = _redirectToLoginLiveData.value

    fun navigate() {
        collectFlow(
            flowTask = fetchUserUseCase::invoke,
            onSuccessCollected = { user ->
                when {
                    user == null || !user.isRememberMe -> {
                        _redirectToLoginLiveData.value = user
                    }
                    user.isRememberMe -> doLogin(user)
                }
            }
        )
    }

    private fun doLogin(user: UserModel) {
        collectFlow(onSuccessLiveData = _onLoginResponseLiveData) {
            doLoginUseCase.invoke(
                DoLoginUseCase.Params(
                    request = LoginRequest(
                        email = user.email,
                        pwd = user.pwd!!
                    ),
                    isRememberMe = true
                )
            )
        }
    }
}
