package pl.md.cardmanager.util

import android.content.Context

class SharedPreferencesUtils {
    companion object{
        private const val preferencesPrefix = "pl.md."

        fun getBoolean(context: Context, key: String): Boolean {
            val sharedPref = context.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE)
            return sharedPref.getBoolean("${preferencesPrefix}$key", true)
        }

        fun getString(context: Context, key: String, defaultValue: String = ""): String {
            val sharedPref = context.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE)
            return sharedPref.getString("$preferencesPrefix$key", defaultValue)!!
        }

        fun putString(context: Context, key: String, value: String) {
            val sharedPref =
                context.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putString("$preferencesPrefix$key", value)
                commit()
            }
        }

        fun putBoolean(context: Context, key: String, value: Boolean) {
            val sharedPref =
                context.getSharedPreferences(preferencesPrefix, Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putBoolean("$preferencesPrefix$key", value)
                commit()
            }
        }
    }
}