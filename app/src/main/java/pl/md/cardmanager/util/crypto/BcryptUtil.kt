package pl.md.cardmanager.util.crypto

import android.util.Base64
import de.nycode.bcrypt.verify

class BcryptUtil {
    companion object {
        fun hash(string: String): String {
            return Base64.encodeToString(de.nycode.bcrypt.hash(string), Base64.NO_WRAP)
        }

        fun verify(input: String, expected: String): Boolean {
            val byteArray = Base64.decode(expected, Base64.NO_WRAP)
            return verify(input, byteArray)
        }
    }
}