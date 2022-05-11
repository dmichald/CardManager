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
import pl.md.cardmanager.ui.login.LoginViewModel
import pl.md.cardmanager.util.UserUtils

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        val TAG = MainActivity::class.qualifiedName
        val USER_KEY_PREFIX = "USER_KEY_"
        lateinit var CURRENT_USER_KEY_ALIAS: String
    }

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = UserUtils.getCurrentUserId(this)
        if (userId != UserUtils.NO_USER) {
            UserUtils.loggedUserId = userId
            CURRENT_USER_KEY_ALIAS = USER_KEY_PREFIX + userId
            val intent = Intent(this, CardListActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContent {
            LoginPage(true)
        }

        viewModel.loggedUserId.observe(this, Observer {
            if (it != UserUtils.NO_USER) {
                UserUtils.saveUserToSharedPref(this, it)
                Log.d(TAG, "Save user to shared pref : '$it'")
                UserUtils.loggedUserId = it
                CURRENT_USER_KEY_ALIAS = USER_KEY_PREFIX + it
                val intent = Intent(this, CardListActivity::class.java)
                startActivity(intent)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        UserUtils.putBoolean(applicationContext, UserUtils.FIRST_RUN, false)
    }

}

