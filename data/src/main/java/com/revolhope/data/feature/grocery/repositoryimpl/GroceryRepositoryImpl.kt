package com.revolhope.data.feature.grocery.repositoryimpl

import com.revolhope.data.common.BaseRepositoryImpl
import com.revolhope.data.feature.grocery.datasource.GroceryNetworkDataSource
import com.revolhope.data.feature.grocery.mapper.GroceryMapper
import com.revolhope.domain.common.extensions.onSuccessMapToOrThrow
import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.grocery.model.GroceryItemModel
import com.revolhope.domain.feature.grocery.model.GroceryListModel
import com.revolhope.domain.feature.grocery.repository.GroceryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroceryRepositoryImpl @Inject constructor(
    //private val localDataSource: GroceryLocalDataSource,
    private val networkDataSource: GroceryNetworkDataSource
) : BaseRepositoryImpl(), GroceryRepository {

    override suspend fun fetchGroceryLists(userId: String): Flow<State<List<GroceryListModel>>> =
        stateful {
            networkDataSource.fetchGroceryLists(userId)
                .map { list -> list.map(GroceryMapper::fromListResponseToModel) }
        }

    override suspend fun insertGroceryList(list: GroceryListModel): Flow<State<Boolean>> =
        stateful {
            val grocery = list.let(GroceryMapper::fromListModelToResponse)
            networkDataSource.addOrUpdateGroceryList(
                userId = list.createdBy.id,
                list = grocery
            ).onSuccessMapToOrThrow {
                list.sharedWith?.id?.let { id ->
                    // TODO This result will not be used right now, before Flow implementation it was
                    //  the result returned
                    networkDataSource.addOrUpdateGroceryList(
                        userId = id,
                        list = grocery
                    ).firstOrNull() ?: false
                } ?: false
            }
        }

    override suspend fun updateGroceryList(list: GroceryListModel): Flow<State<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun removeGroceryList(list: GroceryListModel): Flow<State<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertGroceryItems(
        items: List<GroceryItemModel>,
        listId: String
    ): Flow<State<Boolean>> {
        TODO("Not yet implemented")
    }
}
