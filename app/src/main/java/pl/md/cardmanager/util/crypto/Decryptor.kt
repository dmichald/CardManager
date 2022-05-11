package pl.md.cardmanager.util.crypto

import java.io.IOException
import java.nio.charset.Charset
import java.security.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.GCMParameterSpec

class Decryptor {

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"

        @Throws(
            UnrecoverableEntryException::class,
            NoSuchAlgorithmException::class,
            KeyStoreException::class,
            NoSuchProviderException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class,
            IOException::class,
            BadPaddingException::class,
            IllegalBlockSizeException::class,
            InvalidAlgorithmParameterException::class
        )
        fun decryptData(
            keyAlias: String,
            encryptedData: ByteArray?,
            encryptionIv: ByteArray?
        ): String {
            val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, encryptionIv)
            val key = KeyStoreUtil.getSecretKey(keyAlias)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            return String(cipher.doFinal(encryptedData), Charset.forName("UTF-8"))
        }

    }

}