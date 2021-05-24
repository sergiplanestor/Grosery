package com.revolhope.presentation.library.base

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class DiffUtilAdapter<T, V : View>(open val items: MutableList<T>) :
    RecyclerView.Adapter<AdapterViewWrapper<V>>() {

    // Abstract methods and util methods

    abstract fun onCreateItemView(parent: ViewGroup, viewType: Int): V

    abstract fun onBindView(view: V, item: T)

    abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean

    abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean

    protected fun indexOf(item: T): Int? = items.indexOf(item).takeUnless { it == -1 }

    protected fun itemAt(position: Int): T? = items.getOrNull(position)

    // RecyclerView.Adapter methods

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewWrapper<V> =
        AdapterViewWrapper(onCreateItemView(parent, viewType))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AdapterViewWrapper<V>, position: Int) =
        onBindView(holder.view, items[position])

    // DiffUtil methods

    open fun update(newData: List<T>) {
        if (newData.isEmpty()) {
            items.clear()
            notifyDataSetChanged()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val diffResult = calculateDiffResult(newData)
                items.clear()
                items.addAll(newData)
                diffResult.dispatchUpdatesTo(this@DiffUtilAdapter)
            }
        }
    }

    private suspend fun calculateDiffResult(newData: List<T>): DiffUtil.DiffResult =
        withContext(Dispatchers.IO) {
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    if (oldItemPosition <= items.lastIndex && newItemPosition <= newData.lastIndex) {
                        return areItemsTheSame(items[oldItemPosition], newData[newItemPosition])
                    }
                    return false
                }

                override fun getOldListSize() = items.size

                override fun getNewListSize() = newData.size

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    if (oldItemPosition <= items.lastIndex && newItemPosition <= newData.lastIndex) {
                        return areContentsTheSame(items[oldItemPosition], newData[newItemPosition])
                    }
                    return false
                }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                    return getAdapterChangePayload(oldItemPosition, newItemPosition)
                }
            })
        }

    open fun getAdapterChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return null
    }
}
