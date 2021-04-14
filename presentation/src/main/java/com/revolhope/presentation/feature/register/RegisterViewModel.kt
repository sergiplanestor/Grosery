package com.revolhope.presentation.feature.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.common.extensions.generateIdentifier
import com.revolhope.domain.common.extensions.getNewCreationLastLogin
import com.revolhope.domain.common.extensions.getUsername
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.domain.feature.user.usecase.RegisterUserUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import com.revolhope.presentation.library.extensions.launchAsync
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
        if (email.isBlank() || pwd.isBlank()) _errorLiveData.value = "T_FIXME: DEFAULT ERROR"
        launchAsync(
            asyncTask = {
                val model = UserModel(
                    id = generateIdentifier(),
                    name = getUsername(username, email),
                    email = email,
                    pwd = pwd,
                    isRememberMe = isRememberMe,
                    lastLogin = getNewCreationLastLogin()
                )
                registerUserUseCase.invoke(RegisterUserUseCase.Params(user = model))
            },
            onCompleteTask = { state ->
                handleState(
                    state = state,
                    onSuccess = _onRegisterResultLiveData::setValue
                )
            }
        )
    }
}
