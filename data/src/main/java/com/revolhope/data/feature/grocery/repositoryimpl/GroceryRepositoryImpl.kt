package com.revolhope.data.feature.grocery.repositoryimpl

import com.revolhope.data.common.BaseRepositoryImpl
import com.revolhope.data.feature.grocery.datasource.GroceryLocalDataSource
import com.revolhope.data.feature.grocery.datasource.GroceryNetworkDataSource
import com.revolhope.data.feature.grocery.mapper.GroceryMapper
import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.grocery.model.GroceryItemModel
import com.revolhope.domain.feature.grocery.model.GroceryListModel
import com.revolhope.domain.feature.grocery.repository.GroceryRepository
import javax.inject.Inject

class GroceryRepositoryImpl @Inject constructor(
    //private val localDataSource: GroceryLocalDataSource,
    private val networkDataSource: GroceryNetworkDataSource
) : BaseRepositoryImpl(), GroceryRepository {

    override suspend fun fetchGroceryLists(userId: String): State<List<GroceryListModel>> =
        launchStateful {
            networkDataSource.fetchGroceryLists(userId).map(GroceryMapper::fromListResponseToModel)
        }

    override suspend fun insertGroceryList(list: GroceryListModel): State<Boolean> =
        launchStateful {
            val grocery = list.let(GroceryMapper::fromListModelToResponse)
            networkDataSource.addOrUpdateGroceryList(
                userId = list.createdBy.id,
                list = grocery
            ).let { isSuccess ->
                if (isSuccess) {
                    list.sharedWith?.id?.let { id ->
                        networkDataSource.addOrUpdateGroceryList(
                            userId = id,
                            list = grocery
                        )
                    } ?: isSuccess
                } else {
                    isSuccess
                }
            }
        }

    override suspend fun updateGroceryList(list: GroceryListModel): State<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeGroceryList(list: GroceryListModel): State<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun insertGroceryItems(
        items: List<GroceryItemModel>,
        listId: String
    ): State<Boolean> {
        TODO("Not yet implemented")
    }
}
