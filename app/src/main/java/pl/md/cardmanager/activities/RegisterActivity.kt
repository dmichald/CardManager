package pl.md.cardmanager.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import pl.md.cardmanager.MainActivity
import pl.md.cardmanager.ui.auth.AuthenticationViewModel
import pl.md.cardmanager.ui.auth.LoginPage
import pl.md.cardmanager.util.UserUtils

@AndroidEntryPoint
class RegisterActivity : ComponentActivity() {

    private val viewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginPage(isLogin = false)
        }

        viewModel.loggedUserId.observe(this) {
            if (it != UserUtils.NO_USER.toString()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

}