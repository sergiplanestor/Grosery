package com.revolhope.presentation.feature.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.common.extensions.getNewCreationLastLogin
import com.revolhope.domain.common.extensions.getUsername
import com.revolhope.domain.common.extensions.randomId
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.domain.feature.authentication.usecase.RegisterUserUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : BaseViewModel() {

    private val _onRegisterResultLiveData = MutableLiveData<Boolean>()
    val onRegisterResultLiveData: LiveData<Boolean> get() = _onRegisterResultLiveData

    fun doRegister(
        username: String?,
        email: String,
        pwd: String,
        isRememberMe: Boolean
    ) {
        collectOn(
            task = {
                registerUserUseCase.invoke(
                    RegisterUserUseCase.Params(
                        user = UserModel(
                            id = randomId(),
                            name = getUsername(username, email),
                            email = email,
                            pwd = pwd,
                            isRememberMe = isRememberMe,
                            lastLogin = getNewCreationLastLogin()
                        )
                    )
                )
            },
            onTaskSuccess = _onRegisterResultLiveData::setValue
        )
    }
}
