package pl.md.cardmanager.activities

import LoginPage
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import pl.md.cardmanager.MainActivity
import pl.md.cardmanager.ui.login.LoginViewModel
import pl.md.cardmanager.util.UserUtils
import pl.md.cardmanager.util.crypto.KeyStoreUtil

@AndroidEntryPoint
class RegisterActivity : ComponentActivity() {
    companion object {
        val TAG = RegisterActivity::class.qualifiedName
    }

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginPage(isLogin = false)
        }

        viewModel.loggedUserId.observe(this, Observer {
            if (it != UserUtils.NO_USER) {
                val userKeyAlias = MainActivity.USER_KEY_PREFIX + it
                KeyStoreUtil.generateAndSave(userKeyAlias)
                Log.d(TAG, "Generate key for user: ${it}")
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        })
    }


}