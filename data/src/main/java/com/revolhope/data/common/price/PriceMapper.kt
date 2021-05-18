package com.revolhope.data.common.price

import com.revolhope.domain.common.model.PriceModel


object PriceMapper {
    fun parseToModel(value: Float): PriceModel = PriceModel(value = value)
}
