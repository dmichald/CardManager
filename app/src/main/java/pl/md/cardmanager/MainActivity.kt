package pl.md.cardmanager

import LoginPage
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import pl.md.cardmanager.activities.CardListActivity
import pl.md.cardmanager.ui.login.AuthenticationViewModel
import pl.md.cardmanager.util.SharedPreferencesUtils
import pl.md.cardmanager.util.UserUtils

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        val TAG = MainActivity::class.qualifiedName
        lateinit var CURRENT_USER_KEY_ALIAS: String
    }

    private val viewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // UserUtils.clearUser(this)

        val userId = UserUtils.getCurrentUserId(this)
        if (userId != UserUtils.NO_USER) {
            UserUtils.loggedUserId = UserUtils.getCurrentUserId(this)
            CURRENT_USER_KEY_ALIAS = UserUtils.getUserSecretKeyAlias(applicationContext)
            val intent = Intent(this, CardListActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContent {
            LoginPage(true)
        }

        viewModel.loggedUserKeyAlias.observe(this, Observer {
            val emptyGuid = "00000000-0000-0000-0000-000000000000"
            if (it != emptyGuid) {
                UserUtils.putUserSecretKeyAlias(applicationContext, it)
            }
        })

        viewModel.loggedUserId.observe(this, Observer {
            if (it != UserUtils.NO_USER.toString()) {
                UserUtils.saveUserToSharedPref(this, it)
                Log.d(TAG, "Save user to shared pref : '$it'")
                UserUtils.loggedUserId = UserUtils.getCurrentUserId(this)
                CURRENT_USER_KEY_ALIAS = UserUtils.getUserSecretKeyAlias(applicationContext)
                val intent = Intent(this, CardListActivity::class.java)
                startActivity(intent)
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        SharedPreferencesUtils.putBoolean(
            applicationContext,
            UserUtils.FIRST_RUN_SHARED_PREF,
            false
        )
    }

}

