package com.revolhope.domain.feature.authentication.request

data class LoginRequest(
    val email: String,
    val pwd: String
)
