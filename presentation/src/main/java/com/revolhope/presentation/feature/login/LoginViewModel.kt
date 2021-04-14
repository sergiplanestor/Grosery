package com.revolhope.presentation.feature.login

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
class LoginViewModel @Inject constructor(private val doLoginUseCase: DoLoginUseCase) :
    BaseViewModel() {

    val loginResponseLiveData: LiveData<Boolean> get() = _loginResponseLiveData
    private val _loginResponseLiveData = MutableLiveData<Boolean>()

    fun doLogin(
        email: String,
        pwd: String,
        isRememberMe: Boolean
    ) {
        launchAsync(
            asyncTask = {
                doLoginUseCase.invoke(
                    DoLoginUseCase.Params(
                        request = LoginRequest(
                            email = email,
                            pwd = pwd
                        ),
                        isRememberMe = isRememberMe
                    )
                )
            },
            onCompleteTask = {
                handleState(
                    state = it,
                    onSuccess = _loginResponseLiveData::setValue
                )
            }
        )
    }

}
