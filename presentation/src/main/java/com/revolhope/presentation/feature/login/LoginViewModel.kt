package com.revolhope.presentation.feature.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.authentication.request.LoginRequest
import com.revolhope.domain.feature.authentication.usecase.DoLoginUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val doLoginUseCase: DoLoginUseCase) :
    BaseViewModel() {

    val loginResponseLiveData: LiveData<Boolean> get() = _loginResponseLiveData
    private val _loginResponseLiveData = MutableLiveData<Boolean>()

    fun doLogin(
        email: String,
        pwd: String,
        isRememberMe: Boolean
    ) {
        invokeUseCase(
            useCase = doLoginUseCase,
            request = DoLoginUseCase.RequestParams(
                request = LoginRequest(
                    email = email,
                    pwd = pwd
                ),
                isRememberMe = isRememberMe
            ),
            loadingModel = null,
            onSuccessLiveData = _loginResponseLiveData
        )
    }

}
