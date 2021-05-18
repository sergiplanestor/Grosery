package com.revolhope.domain.feature.grocery.usecase

import com.revolhope.domain.common.model.State
import com.revolhope.domain.feature.grocery.model.GroceryListModel
import com.revolhope.domain.feature.grocery.repository.GroceryRepository
import javax.inject.Inject

class CreateGroceryListUseCase @Inject constructor(
    private val groceryRepository: GroceryRepository,
) {
    suspend operator fun invoke(params: Params): State<Boolean> =
        groceryRepository.insertGroceryList(params.list)

    data class Params(val list: GroceryListModel)
}
