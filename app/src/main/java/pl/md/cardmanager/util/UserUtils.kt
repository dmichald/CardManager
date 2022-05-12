package pl.md.cardmanager.util

import android.content.Context
import android.util.Base64
import androidx.activity.ComponentActivity
import pl.md.cardmanager.util.crypto.Decryptor

class UserUtils {
    companion object {
        const val NO_USER = -1L
        var loggedUserId = NO_USER

        private const val CURRENT_USER_ID_SHARED_PREF = "CURRENT_USER_ID"
        const val FIRST_RUN_SHARED_PREF = "FIRST_RUN"
        const val DB_KEY_SHARED_PREF = "DB_KEY"
        const val DB_KEY_IV_SHARED_PREF = "DB_KEY_IV"
        const val USER_SECRET_KEY_ALIAS_SHARED_PREF = "USER_KEY_ALIAS"

        fun putUserSecretKeyAlias(context: Context, secretKeyAlias: String) {
            SharedPreferencesUtils.putString(
                context,
                USER_SECRET_KEY_ALIAS_SHARED_PREF,
                secretKeyAlias
            )
        }

        fun getUserSecretKeyAlias(context: Context): String {
            return SharedPreferencesUtils.getString(
                context,
                USER_SECRET_KEY_ALIAS_SHARED_PREF
            )
        }

        fun getCurrentUserId(activity: ComponentActivity): Long {
            val userVal = SharedPreferencesUtils.getString(
                activity.applicationContext,
                CURRENT_USER_ID_SHARED_PREF,
                NO_USER.toString()
            )
            val currentUserKeyAlias = getUserSecretKeyAlias(activity.applicationContext)
            return if (userVal == NO_USER.toString()) {
                NO_USER
            } else {
                val encryption = userVal.split("#")[0]
                val encryptionIV = userVal.split("#")[1]
                val encryptionAsByteArray = Base64.decode(encryption, Base64.NO_WRAP)
                val encryptionIVAsByteArray = Base64.decode(encryptionIV, Base64.NO_WRAP)
                val decryptedId = Decryptor.decryptData(
                    encryptedData = encryptionAsByteArray,
                    encryptionIv = encryptionIVAsByteArray,
                    keyAlias = currentUserKeyAlias
                )
                decryptedId.toLong()
            }
        }

        fun getCurrentUserId(context: Context): Long {
            return getCurrentUserId((context as ComponentActivity))
        }

        fun saveUserToSharedPref(activity: ComponentActivity, userId: String) {
            SharedPreferencesUtils.putString(
                activity.applicationContext,
                CURRENT_USER_ID_SHARED_PREF,
                userId
            )
        }

        fun clearUser(activity: ComponentActivity) {
            saveUserToSharedPref(activity, NO_USER.toString())
        }
    }
}

