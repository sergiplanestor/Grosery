package com.revolhope.presentation.feature.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.user.usecase.FetchUserUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import com.revolhope.presentation.library.extensions.launchAsync
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fetchUserUseCase: FetchUserUseCase
) : BaseViewModel() {

    enum class Nav {
        LOGIN,
        REGISTER,
        DASHBOARD
    }

    val onNavigateLiveData: LiveData<Nav> get() = _onNavigateLiveData
    private val _onNavigateLiveData = MutableLiveData<Nav>()

    fun navigate() {
        launchAsync(
            asyncTask = fetchUserUseCase::invoke,
            onCompleteTask = { state ->
                handleState(
                    state = state,
                    onSuccess = { user ->
                        _onNavigateLiveData.value = when {
                            user == null -> Nav.REGISTER
                            user.isRememberMe -> Nav.DASHBOARD
                            else /* user != null && !user.isRememberMe */ -> Nav.LOGIN
                        }
                    }
                )
            }
        )
    }

}