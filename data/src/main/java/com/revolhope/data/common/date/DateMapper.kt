package com.revolhope.data.common.date

import com.revolhope.domain.common.model.DateModel

object DateMapper {
    fun parseToModel(value: Long): DateModel = DateModel(value = value)
}
