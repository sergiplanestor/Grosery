package com.revolhope.domain.feature.profile.model

import kotlin.random.Random

enum class ProfileAvatar(var id: Int) {
    CAT(1),
    LION(2),
    OSTRICH(3),
    OWL(4),
    ZEBRA(5),
    UNICORN(6),
    NONE(-1);

    companion object {
        fun fromId(id: Int?) : ProfileAvatar =
            values().associateBy { it.id }[id] ?: NONE

        fun random(): ProfileAvatar =
            fromId(Random.nextInt(from = 1, until = 7))
    }
}
