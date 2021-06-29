package com.revolhope.domain.feature.grocery.usecase

import com.revolhope.domain.common.base.UseCase
import com.revolhope.domain.feature.grocery.model.GroceryListModel
import com.revolhope.domain.feature.grocery.repository.GroceryRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class CreateGroceryListUseCase @Inject constructor(
    private val groceryRepository: GroceryRepository,
) : UseCase<CreateGroceryListUseCase.RequestParams, Boolean>() {

    override suspend fun build(
        scope: CoroutineScope,
        requestParams: RequestParams
    ): UseCaseParams<RequestParams, Boolean> =
        UseCaseParams {
            groceryRepository.insertGroceryList(requestParams.list)
        }

    data class RequestParams(val list: GroceryListModel)
}
