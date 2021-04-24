package com.revolhope.presentation.feature.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.domain.feature.user.request.LoginRequest
import com.revolhope.domain.feature.user.usecase.DoLoginUseCase
import com.revolhope.domain.feature.user.usecase.FetchUserUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import com.revolhope.presentation.library.extensions.launchAsync
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
        launchAsync(
            asyncTask = fetchUserUseCase::invoke,
            onCompleteTask = { state ->
                handleState(
                    state = state,
                    onSuccess = { user ->
                        when {
                            user == null || !user.isRememberMe -> {
                                _redirectToLoginLiveData.value = user
                            }
                            user.isRememberMe -> doLogin(user)
                        }
                    }
                )
            }
        )
    }

    private fun doLogin(user: UserModel) {
        launchAsync(
            asyncTask = {
                doLoginUseCase.invoke(
                    DoLoginUseCase.Params(
                        request = LoginRequest(
                            email = user.email,
                            pwd = user.pwd!!
                        ),
                        isRememberMe = true
                    )
                )
            },
            onCompleteTask = { state ->
                handleState(
                    state = state,
                    onSuccess = _onLoginResponseLiveData::setValue
                )
            }
        )
    }
}
