package pl.md.cardmanager.util

import android.content.Context
import androidx.activity.ComponentActivity

class UserUtils {
    companion object {
        const val NO_USER = -1L
        var loggedUserId = NO_USER
        private const val preferencesPrefix = "pl.md."
        private const val currentUserKey = preferencesPrefix + "CURRENT_USER_ID"
        private const val ksAlias = "LOGGED_USER"
        const val FIRST_RUN = "FIRST_RUN"
        const val DB_KEY = "DB_KEY"
        const val DB_KEY_IV = "DB_KEY_IV"
        fun getCurrentUserId(activity: ComponentActivity): Long {
            val sharedPref = activity.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE)
            return sharedPref.getLong(currentUserKey, NO_USER)
        }

        fun getCurrentUserId(context: Context): Long {
            val sharedPref = context.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE)
            return sharedPref.getLong(currentUserKey, NO_USER)
        }

        fun saveUserToSharedPref(activity: ComponentActivity, userId: Long) {
            val sharedPref =
                activity.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putLong(currentUserKey, userId)
                commit()
            }
        }

        fun clearUser(activity: ComponentActivity) {
            saveUserToSharedPref(activity, NO_USER)
        }

        fun putBoolean(context: Context, key: String, value: Boolean) {
            val sharedPref =
                context.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putBoolean("$preferencesPrefix$key", value)
                commit()
            }
        }

        fun getString(context: Context, key: String): String {
            val sharedPref = context.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE)
            return sharedPref.getString("$preferencesPrefix$key", "")!!
        }

        fun putString(context: Context, key: String, value: String) {
            val sharedPref =
                context.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putString("$preferencesPrefix$key", value)
                commit()
            }
        }

        fun getBoolean(context: Context, key: String): Boolean {
            val sharedPref = context.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE)
            return sharedPref.getBoolean("$preferencesPrefix$key", true)
        }

    }
}
