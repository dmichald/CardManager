package pl.md.cardmanager.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import pl.md.cardmanager.ui.auth.AuthenticateScreen
import pl.md.cardmanager.ui.auth.AuthenticationViewModel

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    private val viewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.authPassed.observe(this) {
            if (it) {
                val extras = getCardIdFromExtras()
                startAddEditCardActivity(extras.first, extras.second)
            }
        }
        setContent {
            AuthenticateScreen()
        }

    }

    private fun getCardIdFromExtras(): Pair<Long, Boolean> {
        val extras = intent.extras
        var value = AddEditCardActivity.NO_CREDIT_CARD
        var enabled = true
        if (extras != null) {
            value = extras.getLong(AddEditCardActivity.CREDIT_CARD_ID)
            enabled = extras.getBoolean(AddEditCardActivity.CARD_VIEW_ENABLED)
        }
        return Pair(value, enabled)
    }

    private fun startAddEditCardActivity(cardId: Long, readOnly: Boolean) {
        val intent = Intent(applicationContext, AddEditCardActivity::class.java)
        intent.putExtra(AddEditCardActivity.CARD_VIEW_ENABLED, readOnly)
        intent.putExtra(AddEditCardActivity.CREDIT_CARD_ID, cardId)
        startActivity(intent)
        finish()
    }
}