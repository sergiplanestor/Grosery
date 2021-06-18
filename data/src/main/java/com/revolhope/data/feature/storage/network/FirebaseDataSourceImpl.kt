package com.revolhope.data.feature.storage.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.revolhope.data.common.base.BaseDataSourceImpl
import com.revolhope.data.common.exceptions.NoEmailException
import com.revolhope.data.common.extensions.*
import com.revolhope.data.feature.grocery.datasource.GroceryNetworkDataSource
import com.revolhope.data.feature.grocery.response.GroceryListResponse
import com.revolhope.data.feature.profile.datasource.ProfileNetworkDataSource
import com.revolhope.data.feature.profile.response.ProfileResponse
import com.revolhope.data.feature.user.datasource.UserNetworkDataSource
import com.revolhope.data.feature.user.response.UserNetResponse
import com.revolhope.domain.common.extensions.letOrThrow
import com.revolhope.domain.common.extensions.runOnCallbackFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor() : BaseDataSourceImpl(), UserNetworkDataSource,
    GroceryNetworkDataSource, ProfileNetworkDataSource {

    companion object {
        private const val REF_DB = "db/"
        const val REF_USR = "${REF_DB}usr/"
        const val PATH_LIST = "lists/"
        const val PATH_PROFILE = "profile/"
        const val REF_NOTIFICATIONS = "${REF_DB}notify/"
    }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    private val userRef get() = database.getReference(REF_USR)
    private val notificationRef get() = database.getReference(REF_NOTIFICATIONS)
    private fun listRef(userId: String) =
        database.getReference("${REF_DB}${userId}/${PATH_LIST}")

    private fun profileRef(userId: String) =
        database.getReference("${REF_DB}${userId}/${PATH_PROFILE}")

// -------------------------------------------------------------------------------------------------
// UserNetworkDataSource
// -------------------------------------------------------------------------------------------------

    override suspend fun createUserWithEmailAndPassword(email: String, pwd: String): Flow<Boolean> =
        runOnCallbackFlow {
            auth.createUserWithEmailAndPassword(email, pwd).offerOnCompletedOrThrow(this)
        }

    override suspend fun signInWithEmailAndPassword(email: String, pwd: String): Flow<Boolean> =
        runOnCallbackFlow {
            auth.signInWithEmailAndPassword(email, pwd).offerOnCompletedOrThrow(this)
        }

    override suspend fun fetchUserDataByEmail(email: String): Flow<UserNetResponse?> =
        runOnCallbackFlow {
            userRef.child(email.sha1).offerOnSingleValue(producerScope = this) { data ->
                data.fetchJsonTo(UserNetResponse::class)
            }
        }

    override suspend fun insertUser(userNetResponse: UserNetResponse): Flow<Boolean> =
        runOnCallbackFlow {
            userNetResponse.email.letOrThrow(NoEmailException()) {
                userRef.child(it.sha1)
                    .insertAsJson(userNetResponse)
                    .offerOnCompletedOrThrow(this)
            }
        }

// -------------------------------------------------------------------------------------------------
// GroceryNetworkDataSource
// -------------------------------------------------------------------------------------------------

    override suspend fun fetchGroceryLists(userId: String): Flow<List<GroceryListResponse>> =
        runOnCallbackFlow {
            listRef(userId).offerOnSingleValue(producerScope = this) { data ->
                data.children.mapNotNull {
                    it.fetchJsonTo(GroceryListResponse::class)
                }
            }
        }

    override suspend fun addOrUpdateGroceryList(
        userId: String,
        list: GroceryListResponse
    ): Flow<Boolean> =
        runOnCallbackFlow {
            listRef(userId).pushAsJson(list).offerOnCompletedOrThrow(this)
        }

// -------------------------------------------------------------------------------------------------
// ProfileNetworkDataSource
// -------------------------------------------------------------------------------------------------

    override suspend fun fetchProfile(userId: String): Flow<ProfileResponse?> =
        runOnCallbackFlow {
            profileRef(userId).offerOnSingleValue(producerScope = this) { data ->
                data.fetchJsonTo(ProfileResponse::class)
            }
        }

    override suspend fun insertOrUpdateProfile(
        userId: String,
        profile: ProfileResponse
    ): Flow<Boolean> =
        runOnCallbackFlow {
            // TODO: Refactor to new impl.
            /*runOnSuspendedOrFalse { cont ->
                profileRef(userId).addOnSingleEventListener(
                    continuation = cont,
                    behavior = FlowEmissionBehavior.EMIT_WITH_EXCEPTION_ANY_CASE,
                    onReceivedBlock = { data ->
                        val addProfileMethod = {
                            profileRef(userId).insertAsJson(profile)
                                .addResumeOnCompleteListener(cont)
                        }
                        if (data.children.count() != 0) {
                            profileRef(userId).removeValue { error, _ ->
                                if (error == null) {
                                    addProfileMethod.invoke()
                                } else {
                                    cont.resumeWithException(error.toException())
                                }
                            }
                        } else {
                            addProfileMethod.invoke()
                        }
                        true // Dummy return data, it doesn't affect
                    }
                )
            }*/
        }
}
