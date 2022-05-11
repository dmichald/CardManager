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
import pl.md.cardmanager.util.crypto.Decryptor
import pl.md.cardmanager.util.crypto.EnCryptor
import pl.md.cardmanager.util.crypto.KeyStoreUtil

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        val TAG = MainActivity::class.qualifiedName
        val CURRENT_USER_KEY_ALIAS = "USER_KEY_${UserUtils.loggedUserId}"
    }

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (false) {
            UserUtils.clearUser(this)
        }

        val userId = UserUtils.getCurrentUserId(this)
        if (userId != UserUtils.NO_USER) {
            UserUtils.loggedUserId = userId
            val intent = Intent(this, CardListActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContent {
            LoginPage()
        }
        viewModel.loggedUserId.observe(this, Observer {
            if (it != UserUtils.NO_USER) {
                UserUtils.saveUserToSharedPref(this, it)
                Log.d(TAG, "Save user to shared pref : '$it'")
                UserUtils.loggedUserId = it
                val intent = Intent(this, CardListActivity::class.java)
                startActivity(intent)
                if(isFirstRun()){
                    KeyStoreUtil.generateAndSave(CURRENT_USER_KEY_ALIAS)
                    Log.d(TAG, "Generate key for user: ${UserUtils.loggedUserId}")
                }
            }
        })


        val toEncrypt = "TO ENCRYPT"
        val keyalias = "ALIASK"
        val key = KeyStoreUtil.generateAndSave(keyalias)
        val getKey = KeyStoreUtil.getSecretKey(keyalias)
        val encryptor = EnCryptor.encryptText(getKey, toEncrypt)
        val dec = Decryptor.decryptData(keyalias, encryptor.encryption, encryptor.encryptionIV)
        Log.d(TAG, "DECRYPTED: $dec")
    }

    override fun onDestroy() {
        super.onDestroy()
        UserUtils.putBoolean(applicationContext, UserUtils.FIRST_RUN, false)
    }

    private fun isFirstRun(): Boolean {
        return UserUtils.getBoolean(applicationContext, UserUtils.FIRST_RUN)
    }
}

