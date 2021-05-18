package com.revolhope.domain.common.extensions

import com.revolhope.domain.common.model.DateModel
import java.util.UUID

fun getUsername(username: String?, email: String) =
    username ?: if (email.contains("@")) {
        email.split("@")[0]
    } else {
        email
    }

fun getNewCreationLastLogin() =
    DateModel(value = System.currentTimeMillis())
