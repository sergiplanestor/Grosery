package com.revolhope.domain.common.extensions

import com.revolhope.domain.common.model.PriceModel
import com.revolhope.domain.feature.grocery.model.GroceryItemModel

inline val List<GroceryItemModel>.sumAllTotalPrices: PriceModel? get() =
    if (all { it.totalPrice != null }) {
        PriceModel(value = sumByDouble { it.totalPrice!!.value.toDouble() }.toFloat())
    } else {
        null
    }

inline val List<GroceryItemModel>.sumExistingTotalPrices: PriceModel? get() =
    if (any { it.totalPrice != null }) {
        PriceModel(
            value = filter { it.totalPrice != null }.sumByDouble {
                it.totalPrice!!.value.toDouble()
            }.toFloat())
    } else {
        null
    }
