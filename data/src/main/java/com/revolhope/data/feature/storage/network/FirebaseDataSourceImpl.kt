package com.revolhope.data.feature.storage.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.revolhope.data.common.exceptions.NoEmailException
import com.revolhope.data.common.extensions.ContinuationBehavior
import com.revolhope.data.common.extensions.addOnSingleEventListener
import com.revolhope.data.common.extensions.addResumeOnCompleteListener
import com.revolhope.data.common.extensions.fetchOnSuspended
import com.revolhope.data.common.extensions.runOnSuspended
import com.revolhope.data.common.extensions.sha1
import com.revolhope.data.common.extensions.valueJSON
import com.revolhope.data.feature.grocery.datasource.GroceryNetworkDataSource
import com.revolhope.data.feature.grocery.response.GroceryListResponse
import com.revolhope.data.feature.profile.datasource.ProfileNetworkDataSource
import com.revolhope.data.feature.profile.response.ProfileResponse
import com.revolhope.data.feature.user.datasource.UserNetworkDataSource
import com.revolhope.data.feature.user.response.UserNetResponse
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class FirebaseDataSourceImpl @Inject constructor() : UserNetworkDataSource,
    GroceryNetworkDataSource, ProfileNetworkDataSource {

    companion object {
        private const val REF_DB = "db/"
        const val REF_USR = "${REF_DB}usr/"
        const val PATH_LIST = "lists/"
        const val PATH_PROFILE = "profile/"
        const val REF_NOTIFICATIONS = "${REF_DB}notify/"
    }

    /*TODO set private and remove override */ override val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    private val userRef get() = database.getReference(REF_USR)
    private val notificationRef get() = database.getReference(REF_NOTIFICATIONS)
    private fun listRef(userId: String) =
        database.getReference("${REF_DB}${userId}/${PATH_LIST}")

    private fun profileRef(userId: String) =
        database.getReference("${REF_DB}${userId}/${PATH_PROFILE}")

    // UserNetworkDataSource -----------------------------------------------------------------------

    override suspend fun createUserWithEmailAndPassword(email: String, pwd: String): Boolean =
        runOnSuspended {
            auth.createUserWithEmailAndPassword(email, pwd).addResumeOnCompleteListener(this)
        }

    override suspend fun signInWithEmailAndPassword(email: String, pwd: String): Boolean =
        runOnSuspended {
            auth.signInWithEmailAndPassword(email, pwd).addResumeOnCompleteListener(this)
        }

    override suspend fun fetchUserDataByEmail(email: String): UserNetResponse? =
        userRef.child(email.sha1).fetchOnSuspended<UserNetResponse?> { data ->
            data.valueJSON(UserNetResponse::class)
        }

    override suspend fun insertUser(userNetResponse: UserNetResponse): Boolean =
        runOnSuspended {
            userNetResponse.email?.let {
                userRef.child(it.sha1).valueJSON(userNetResponse).addResumeOnCompleteListener(this)
            } ?: resumeWithException(NoEmailException())
        }

    // GroceryNetworkDataSource --------------------------------------------------------------------

    override suspend fun fetchGroceryLists(userId: String): List<GroceryListResponse> =
        listRef(userId).fetchOnSuspended { data ->
            data.children.mapNotNull {
                it.valueJSON(GroceryListResponse::class)
            }
        }

    override suspend fun addOrUpdateGroceryList(
        userId: String,
        list: GroceryListResponse
    ): Boolean =
        runOnSuspended {
            listRef(userId).push().valueJSON(list).addResumeOnCompleteListener(this)
        }

    // ProfileNetworkDataSource --------------------------------------------------------------------

    override suspend fun fetchProfile(userId: String): ProfileResponse? =
        profileRef(userId).fetchOnSuspended<ProfileResponse?>(behavior = ContinuationBehavior.RESUME_WITH_EXCEPTION_ON_CANCELLED) { data ->
            data.valueJSON(ProfileResponse::class)
        }

    override suspend fun insertOrUpdateProfile(userId: String, profile: ProfileResponse): Boolean =
        runOnSuspended {
            profileRef(userId).addOnSingleEventListener(
                continuation = this,
                behavior = ContinuationBehavior.RESUME_WITH_EXCEPTION_ANY_CASE,
                onReceivedBlock = { data ->
                    val addProfileMethod = {
                        profileRef(userId).valueJSON(profile).addResumeOnCompleteListener(this)
                    }
                    if (data.children.count() != 0) {
                        profileRef(userId).removeValue { error, _ ->
                            if (error == null) {
                                addProfileMethod.invoke()
                            } else {
                                resumeWithException(error.toException())
                            }
                        }
                    } else {
                        addProfileMethod.invoke()
                    }
                    true // Dummy return data, it doesn't affect
                }
            )
        }
}
