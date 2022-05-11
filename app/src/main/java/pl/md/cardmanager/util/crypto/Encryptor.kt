package pl.md.cardmanager.util.crypto

import java.io.IOException
import java.security.*
import javax.crypto.*


class EnCryptor {

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val CHARSET = "UTF-8"

        @Throws(
            UnrecoverableEntryException::class,
            NoSuchAlgorithmException::class,
            KeyStoreException::class,
            NoSuchProviderException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class,
            IOException::class,
            InvalidAlgorithmParameterException::class,
            SignatureException::class,
            BadPaddingException::class,
            IllegalBlockSizeException::class
        )
        fun encryptText(secretKey: SecretKey, toEncrypt: String): EncryptorResult {
            val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encryption = cipher.doFinal(toEncrypt.toByteArray(charset(CHARSET)))
            return EncryptorResult(encryption, iv)
        }
    }
}