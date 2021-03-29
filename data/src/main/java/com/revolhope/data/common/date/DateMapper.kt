package com.revolhope.data.common.date

import android.annotation.SuppressLint
import com.revolhope.domain.common.model.DateModel
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("ConstantLocale")
object DateMapper {

    private const val FORMAT = "dd/MM/yyyy"
    private val sdf by lazy { SimpleDateFormat(FORMAT, Locale.getDefault()) }

    fun parseToModel(value: Long): DateModel =
        DateModel(
            value = value,
            formatted = sdf.format(value)
        )
}