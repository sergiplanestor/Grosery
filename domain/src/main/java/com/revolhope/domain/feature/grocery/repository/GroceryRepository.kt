package com.revolhope.domain.feature.grocery.repository

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.grocery.model.GroceryItemModel
import com.revolhope.domain.feature.grocery.model.GroceryListModel
import kotlinx.coroutines.flow.Flow

interface GroceryRepository {

    suspend fun fetchGroceryLists(userId: String): Flow<State<List<GroceryListModel>>>

    suspend fun insertGroceryList(list: GroceryListModel): Flow<State<Boolean>>

    suspend fun updateGroceryList(list: GroceryListModel): Flow<State<Boolean>>

    suspend fun removeGroceryList(list: GroceryListModel): Flow<State<Boolean>>

    suspend fun insertGroceryItems(items: List<GroceryItemModel>, listId: String): Flow<State<Boolean>>
}
