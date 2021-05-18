package com.revolhope.domain.common.extensions

import java.util.UUID

fun generateID() = UUID.randomUUID().toString().replace("-", "")

