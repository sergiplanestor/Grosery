package com.revolhope.presentation.feature.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.user.model.UserModel
import com.revolhope.domain.feature.user.usecase.DoLoginUseCase
import com.revolhope.domain.feature.user.usecase.FetchUserUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import com.revolhope.presentation.library.extensions.launchAsync
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val fetchUserUseCase: FetchUserUseCase,
    private val doLoginUseCase: DoLoginUseCase
) : BaseViewModel() {

    val userLiveData: LiveData<UserModel?> get() = _userLiveData
    private val _userLiveData = MutableLiveData<UserModel?>()

    val loginResponseLiveData: LiveData<Boolean> get() = _loginResponseLiveData
    private val _loginResponseLiveData = MutableLiveData<Boolean>()


    fun fetchUser() {
        launchAsync(
            asyncTask = fetchUserUseCase::invoke,
            onCompleteTask = {
                handleState(
                    state = it,
                    onSuccess = _userLiveData::setValue
                )
            }
        )
    }

    fun doLogin(
        email: String,
        pwd: String,
        isRememberMe: Boolean
    ) {
        /*_userLiveData.value?.let { userModel ->
            launchAsync(
                asyncTask = {
                    doLoginUseCase.invoke(
                        DoLoginUseCase.Params(
                            user = userModel.copy(

                                pwd =
                            )
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
        }*/
    }

}
