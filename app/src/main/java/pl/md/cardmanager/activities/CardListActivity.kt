package pl.md.cardmanager.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.md.cardmanager.MainActivity
import pl.md.cardmanager.data.model.CardInfo
import pl.md.cardmanager.data.model.CreditCardBackupDto
import pl.md.cardmanager.data.model.CreditCardBackupDtoList
import pl.md.cardmanager.data.repository.CreditCardRepository
import pl.md.cardmanager.ui.CardList
import pl.md.cardmanager.util.UserUtils
import pl.md.cardmanager.util.crypto.Decryptor
import pl.md.cardmanager.util.crypto.EnCryptor
import pl.md.cardmanager.util.crypto.KeyStoreUtil
import java.io.*
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class CardListActivity : ComponentActivity() {
    companion object {
        val TAG = CardListActivity::class.qualifiedName
    }

    val WRITE_EXTERNAL_STORAGE_CODE = 101
    val READ_EXTERNAL_STORAGE_CODE = 102

    @Inject
    lateinit var cardRepository: CreditCardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Current logged user: ${UserUtils.getCurrentUserId(this)}")
        setContent {
            CardList(
                onExportClick = { onExportClick(this) },
                onImportClick = { onImportClick() },
                onAddNewClick = { addNewCard() },
                getCards(),
                onDeleteItem = ::deleteItem
            )
        }

    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val uri: Uri = data?.data!!
            it.data!!.data.also {
                GlobalScope.launch {
                    val toastText: String = try {
                        val fileContent = readTextFromUri(uri)
                        val cards = importCards(fileContent)
                        cardRepository.importCards(UserUtils.loggedUserId, cards)
                        Log.d(TAG, "Cards imported. Count: ${cards.size}")
                        "Zaimportowany ${cards.size} karty"
                    }catch (e: Exception){
                        Log.d(TAG, e.message!!)
                        "Nie mozna rozpoznać pliku. Wybierz inny plik."
                    }

                    this@CardListActivity.runOnUiThread {
                        Toast.makeText(applicationContext, toastText, Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }
    }

    private fun importCards(fileContent: String): List<CreditCardBackupDto> {
        val split = fileContent.split("#")
        val encryption = Base64.decode(split.get(0), Base64.NO_WRAP)
        val encryptionIV = Base64.decode(split.get(1), Base64.NO_WRAP)
        val decrypted: String =
            Decryptor.decryptData(MainActivity.CURRENT_USER_KEY_ALIAS, encryption, encryptionIV)
        val jsonElement = Json.parseToJsonElement(decrypted)
        val cards = Json.decodeFromJsonElement(CreditCardBackupDtoList.serializer(), jsonElement)
        Log.d(TAG, "Deserialized cards: $cards")
        return cards.cards
    }


    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun onImportClick() {
        checkForPermission(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "read",
            READ_EXTERNAL_STORAGE_CODE
        )


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            var data = Intent(Intent.ACTION_OPEN_DOCUMENT)
            data.type = "*/*"
            data = Intent.createChooser(data, "Wybierz plik backupu")
            launcher.launch(data)
        }
    }

    private fun onExportClick(activity: CardListActivity) {
        checkForPermission(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "write",
            WRITE_EXTERNAL_STORAGE_CODE
        )
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            GlobalScope.launch() {
                val cards = cardRepository.getUserBackup(UserUtils.loggedUserId)
                var toastText: String
                toastText = if (cards.isEmpty()) {
                    "Brak kart. Dodaj karty."
                } else {
                    exportCards(cards)
                    "Zapisano plik z kartami do folderu 'Pobrane'"
                }
                activity.runOnUiThread {
                    Toast.makeText(applicationContext, toastText, Toast.LENGTH_SHORT).show()
                }

            }


        }

    }

    fun exportCards(cards: List<CreditCardBackupDto>) {
        val cardsAsJson = Json.encodeToString(CreditCardBackupDtoList(cards))
        val key = KeyStoreUtil.getSecretKey(MainActivity.CURRENT_USER_KEY_ALIAS)
        val encrypted = EnCryptor.encryptText(key, cardsAsJson)
        val encryptionAsString = Base64.encodeToString(encrypted.encryption, Base64.NO_WRAP)
        val encryptionIVAsString = Base64.encodeToString(encrypted.encryptionIV, Base64.NO_WRAP)
        val fileContent = "$encryptionAsString#$encryptionIVAsString"
        val uuidString = UUID.randomUUID().toString()
        val fileName = "CardManagerImport$uuidString.txt"
        saveToDownloadsDirectory(fileContent, fileName)
        Log.d(TAG, "Backup created: '$fileName'")
    }

    fun saveToDownloadsDirectory(content: String, fileName: String) {
        var root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        root = File(root, fileName)
        val fout = FileOutputStream(root)
        fout.write(content.toByteArray())
        fout.close()
    }

    private fun deleteItem(itemId: Long) {
        lifecycleScope.launch {
            cardRepository.deleteCardById(itemId)
        }
    }

    private fun addNewCard() {
        val intent = Intent(this, AddEditCardActivity::class.java)
        intent.putExtra(AddEditCardActivity.CREDIT_CARD_ID, AddEditCardActivity.NO_CREDIT_CARD)
        intent.putExtra(AddEditCardActivity.CARD_VIEW_ENABLED, true)
        startActivity(intent)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun getCards(): kotlinx.coroutines.flow.Flow<List<CardInfo>> {
        return cardRepository.getUserCards(UserUtils.loggedUserId)
    }

    private fun checkForPermission(permission: String, name: String, requestCode: Int) {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(
                    applicationContext,
                    "$name uprawnienia przyznane BLABLA",
                    Toast.LENGTH_SHORT
                ).show()
            }
            shouldShowRequestPermissionRationale(permission) -> showDialog(
                permission,
                name,
                requestCode
            )
            else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Uprawnienia $name potrzebne aby uzywac aplikacji")
            setTitle("Uprawnienia potrzebne")
            setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(
                    this@CardListActivity,
                    arrayOf(permission),
                    requestCode
                )
            }
            builder.create().show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    applicationContext,
                    "$name uprawnienia przyznane",
                    Toast.LENGTH_SHORT
                ).show()
                //saveFile()
            } else {
                Toast.makeText(
                    applicationContext,
                    "$name uprawnienia nie przyznane",
                    Toast.LENGTH_SHORT
                ).show()
            }

            when (requestCode) {
                WRITE_EXTERNAL_STORAGE_CODE -> innerCheck("write")
                READ_EXTERNAL_STORAGE_CODE -> innerCheck("read")
            }
        }
    }
}

