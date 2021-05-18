package com.revolhope.data.feature.grocery.datasource

import com.revolhope.data.feature.grocery.response.GroceryListResponse

interface GroceryNetworkDataSource {

    suspend fun fetchGroceryLists(userId: String): List<GroceryListResponse>

    suspend fun addOrUpdateGroceryList(userId: String, list: GroceryListResponse): Boolean

}
