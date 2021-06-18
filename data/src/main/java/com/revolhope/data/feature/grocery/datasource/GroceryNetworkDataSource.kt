package com.revolhope.data.feature.grocery.datasource

import com.revolhope.data.feature.grocery.response.GroceryListResponse
import kotlinx.coroutines.flow.Flow

interface GroceryNetworkDataSource {

    suspend fun fetchGroceryLists(userId: String): Flow<List<GroceryListResponse>>

    suspend fun addOrUpdateGroceryList(userId: String, list: GroceryListResponse): Flow<Boolean>

}
