package pl.md.cardmanager.data.repository

import android.database.Cursor
import kotlinx.coroutines.flow.Flow
import pl.md.cardmanager.data.model.CardInfo
import pl.md.cardmanager.data.model.CreditCard
import pl.md.cardmanager.data.model.CreditCardBackupDto

interface CreditCardRepository {
    suspend fun insertCard(card: CreditCard)
    suspend fun deleteCard(card: CreditCard)
    suspend fun deleteCardById(cardId: Long)
    suspend fun getCardById(cardId: Long): CreditCard
    fun getUserCards(userId: Long): Flow<List<CardInfo>>
    fun getUserCardsCursor(userId: Long): Cursor
    suspend fun getUserBackup(userId: Long): List<CreditCardBackupDto>
}