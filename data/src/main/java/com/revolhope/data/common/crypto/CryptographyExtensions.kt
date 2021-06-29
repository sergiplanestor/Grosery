package com.revolhope.data.common.crypto

import android.security.keystore.KeyProperties
import android.util.Base64
import com.revolhope.data.BuildConfig
import com.revolhope.data.common.extensions.asJson
import com.revolhope.data.common.extensions.fromJsonTo
import com.revolhope.domain.common.extensions.safe
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.reflect.KClass

private val ivSpec by lazy { BuildConfig.NET_IV.decode().let { IvParameterSpec(it) } }
private val secretSpec by lazy {
    BuildConfig.NET_SECRET.decode().let {
        SecretKeySpec(
            it,
            0,
            it.size,
            KeyProperties.KEY_ALGORITHM_AES
        )
    }
}
private const val TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES + "/" +
        KeyProperties.BLOCK_MODE_CBC + "/" +
        KeyProperties.ENCRYPTION_PADDING_PKCS7

inline val <reified T> T.encrypt: String?
    get() =
        cipher(Cipher.ENCRYPT_MODE)?.let {
            it.doFinal(asJson().toByteArray())?.encode()
        }

fun <T : Any> String?.decrypt(clazz: KClass<T>): T? =
    this?.decode()?.let {
        cipher(Cipher.DECRYPT_MODE)?.doFinal(it)?.let { data -> String(data).fromJsonTo(clazz) }
    }

fun cipher(mode: Int): Cipher? = safe {
    Cipher.getInstance(TRANSFORMATION).apply { init(mode, secretSpec, ivSpec) }
}

fun ByteArray.encode(): String = Base64.encodeToString(this, Base64.DEFAULT)

fun String.decode(): ByteArray = Base64.decode(toByteArray(), Base64.DEFAULT)
