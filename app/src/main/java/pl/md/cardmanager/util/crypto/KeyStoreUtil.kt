package pl.md.cardmanager.util.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class KeyStoreUtil {
    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"

        @Throws(
            NoSuchAlgorithmException::class,
            UnrecoverableEntryException::class,
            KeyStoreException::class
        )
        fun getSecretKey(alias: String): SecretKey {
            val keyStore: KeyStore? = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore?.load(null)
            return (keyStore?.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
        }

        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchProviderException::class,
            InvalidAlgorithmParameterException::class
        )
        fun generateAndSave(alias: String): SecretKey {
            val keyGenerator: KeyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
            return keyGenerator.generateKey()
        }
    }
}