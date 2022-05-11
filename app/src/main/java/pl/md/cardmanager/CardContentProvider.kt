package pl.md.cardmanager

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.room.Database
import dagger.hilt.android.AndroidEntryPoint
import pl.md.cardmanager.data.repository.CreditCardRepository
import pl.md.cardmanager.util.UserUtils
import javax.inject.Inject


class CardContentProvider : ContentProvider() {
    private val cardDao by lazy {
        (context!!.applicationContext as CardApp).database.creditCardDao
    }
    override fun onCreate(): Boolean {
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val userId = context?.let { UserUtils.getCurrentUserId(it) }
        return cardDao.getAllCardsCursor(userId!!)
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }
}