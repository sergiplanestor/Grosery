package com.revolhope.presentation.feature.grocerylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.grocery.model.GroceryItemModel
import com.revolhope.domain.feature.grocery.model.GroceryListModel
import com.revolhope.domain.feature.grocery.usecase.CreateGroceryListUseCase
import com.revolhope.domain.feature.authentication.model.UserModel
import com.revolhope.presentation.library.base.BaseViewModel
import com.revolhope.presentation.library.extensions.launchAsync
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GroceryListViewModel @Inject constructor(
    private val createGroceryListUseCase: CreateGroceryListUseCase
) : BaseViewModel() {

    private val onListCreatedLiveData: LiveData<Boolean> get() = _onListCreatedLiveData
    private val _onListCreatedLiveData = MutableLiveData<Boolean>()

    private var isEditing: Boolean = false
    private var _list: GroceryListModel? = null
    private val list: GroceryListModel?
        get() = _list ?: user?.let { GroceryListModel.new(user = it).also { list -> _list = list } }

    var user: UserModel? = null

    private fun insertNewList(list: GroceryListModel) {
        launchAsync(
            asyncTask = { createGroceryListUseCase.invoke(CreateGroceryListUseCase.Params(list)) },
            onTaskCompleted = {
                handleState(
                    state = this,
                    onSuccess = _onListCreatedLiveData::setValue
                )
            }
        )
    }

    private fun updateList(list: GroceryListModel) {

    }

    fun setList(list: GroceryListModel) {
        _list = list
        isEditing = true
    }

    fun addItem(item: GroceryItemModel) {
        list?.items?.add(item)
    }

    fun persistChanges() {
        list?.let {
            if (isEditing) {
                updateList(it)
            } else {
                insertNewList(it)
            }
        }
    }

}
