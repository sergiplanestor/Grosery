package com.revolhope.domain.feature.grocery.repository

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.grocery.model.GroceryItemModel
import com.revolhope.domain.feature.grocery.model.GroceryListModel

interface GroceryRepository {

    suspend fun fetchGroceryLists(userId: String): State<List<GroceryListModel>>

    suspend fun insertGroceryList(list: GroceryListModel): State<Boolean>

    suspend fun updateGroceryList(list: GroceryListModel): State<Boolean>

    suspend fun removeGroceryList(list: GroceryListModel): State<Boolean>

    suspend fun insertGroceryItems(items: List<GroceryItemModel>, listId: String): State<Boolean>
}
