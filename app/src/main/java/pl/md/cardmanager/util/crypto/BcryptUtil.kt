package pl.md.cardmanager.util.crypto

import android.util.Base64
import de.nycode.bcrypt.verify
import java.security.SecureRandom

class BcryptUtil {
    companion object {
        fun hash(string: String): String {
            return Base64.encodeToString(de.nycode.bcrypt.hash(string), Base64.NO_WRAP)
        }

        fun verify(input: String, expected: String): Boolean {
            val byteArray = Base64.decode(expected, Base64.NO_WRAP)
            return verify(input, byteArray)
        }

        fun secureRandom(): String {
            val random = SecureRandom()
            val ba = ByteArray(25)
            random.nextBytes(ba)
            val s = Base64.encodeToString(ba, Base64.NO_WRAP)
            val ba2: ByteArray = de.nycode.bcrypt.hash(s)
            return Base64.encodeToString(ba2, Base64.NO_WRAP)
        }
    }
}