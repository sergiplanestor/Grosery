package com.revolhope.presentation.library.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

data class AdapterViewWrapper<out V : View>(val view: V) : RecyclerView.ViewHolder(view)
