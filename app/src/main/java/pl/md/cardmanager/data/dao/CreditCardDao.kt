package pl.md.cardmanager.data.dao

import android.database.Cursor
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.md.cardmanager.data.model.CardInfo
import pl.md.cardmanager.data.model.CreditCard
import pl.md.cardmanager.data.model.CreditCardBackupDto

@Dao
interface CreditCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CreditCard)

    @Delete
    suspend fun deleteCard(card: CreditCard)

    @Query("DELETE FROM CreditCard WHERE id = :cardId")
    suspend fun deleteCardById(cardId: Long)

    @Query("SELECT * FROM CreditCard WHERE id = :cardId")
    suspend fun getCardById(cardId: Long): CreditCard

    @Query("SELECT id, name, number FROM CreditCard WHERE userId = :userId")
    fun getAllCards(userId: Long): Flow<List<CardInfo>>

    @Query("SELECT name, number, ownerName,expirationDate,CVC FROM CreditCard WHERE userId = :userId")
    fun getAllCardsCursor(userId: Long): Cursor

    @Query("SELECT name, number, ownerName,expirationDate,CVC FROM CreditCard WHERE userId = :userId")
    fun getUserBackup(userId: Long): List<CreditCardBackupDto>
}