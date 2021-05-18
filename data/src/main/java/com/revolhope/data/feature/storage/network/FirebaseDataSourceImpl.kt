package com.revolhope.data.feature.storage.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.revolhope.data.common.extensions.addIsSuccessListener
import com.revolhope.data.common.extensions.addSingleEventListener
import com.revolhope.data.common.extensions.launchFirebaseCall
import com.revolhope.data.common.extensions.sha1
import com.revolhope.data.common.extensions.valueJSON
import com.revolhope.data.feature.grocery.datasource.GroceryNetworkDataSource
import com.revolhope.data.feature.grocery.response.GroceryListResponse
import com.revolhope.data.feature.user.datasource.UserNetworkDataSource
import com.revolhope.data.feature.user.response.UserNetResponse
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseDataSourceImpl @Inject constructor() : UserNetworkDataSource, GroceryNetworkDataSource {

    companion object {
        private const val REF_DB = "db/"
        const val REF_USR = "${REF_DB}usr/"
        const val PATH_LIST = "lists/"
        const val REF_NOTIFICATIONS = "${REF_DB}notify/"
    }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    private val userRef get() = database.getReference(REF_USR)
    private val notificationRef get() = database.getReference(REF_NOTIFICATIONS)
    private fun listRef(userId: String) =
        database.getReference("${REF_DB}${userId}${PATH_LIST}")

    // UserNetworkDataSource

    override suspend fun createUserWithEmailAndPassword(email: String, pwd: String): Boolean =
        launchFirebaseCall { cont ->
            auth.createUserWithEmailAndPassword(email, pwd).addIsSuccessListener(cont)
        }

    override suspend fun signInWithEmailAndPassword(email: String, pwd: String): Boolean =
        launchFirebaseCall { cont ->
            auth.signInWithEmailAndPassword(email, pwd).addIsSuccessListener(cont)
        }

    override suspend fun fetchUserDataByEmail(email: String): UserNetResponse? =
        launchFirebaseCall { cont ->
            userRef.child(email.sha1).addSingleEventListener(
                continuation = cont,
                onReceived = { data -> data.valueJSON(UserNetResponse::class) }
            )
        }

    override suspend fun insertUser(userNetResponse: UserNetResponse): Boolean =
        launchFirebaseCall { cont ->
            userNetResponse.email?.let {
                userRef.child(it.sha1).valueJSON(userNetResponse).addIsSuccessListener(cont)
            } ?: cont.resume(false)
        }

    // GroceryNetworkDataSource

    override suspend fun fetchGroceryLists(userId: String): List<GroceryListResponse> =
        launchFirebaseCall { cont ->
            val list = mutableListOf<GroceryListResponse>()
            listRef(userId).addSingleEventListener(
                continuation = cont,
                onReceived = { data ->
                    data.children.forEach {
                        it.valueJSON(GroceryListResponse::class)?.let(list::add)
                    }
                    list
                }
            )
        }

    override suspend fun addOrUpdateGroceryList(
        userId: String,
        list: GroceryListResponse
    ): Boolean =
        launchFirebaseCall { cont ->
            listRef(userId).push().valueJSON(list).addIsSuccessListener(cont)
        }
}
