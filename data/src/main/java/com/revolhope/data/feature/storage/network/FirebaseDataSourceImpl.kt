package com.revolhope.data.feature.storage.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.revolhope.data.common.extensions.addIsSuccessListener
import com.revolhope.data.common.extensions.launchFirebaseCall
import com.revolhope.data.feature.user.datasource.UserNetworkDataSource
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor() : UserNetworkDataSource {

    companion object {
        const val REF_USR = "db/content/serie"
    }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    override suspend fun createUserWithEmailAndPassword(email: String, pwd: String): Boolean =
        launchFirebaseCall { cont ->
            auth.createUserWithEmailAndPassword(email, pwd).addIsSuccessListener(cont)
        }

    override suspend fun signInWithEmailAndPassword(email: String, pwd: String): Boolean =
        launchFirebaseCall { cont ->
            auth.signInWithEmailAndPassword(email, pwd).addIsSuccessListener(cont)
        }
}
