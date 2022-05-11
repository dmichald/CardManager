package pl.md.cardmanager.data.repository

import android.database.Cursor
import kotlinx.coroutines.flow.Flow
import pl.md.cardmanager.data.dao.CreditCardDao
import pl.md.cardmanager.data.model.CardInfo
import pl.md.cardmanager.data.model.CreditCard
import pl.md.cardmanager.data.model.CreditCardBackupDto

class CreditCardRepositoryImpl(private val dao: CreditCardDao) : CreditCardRepository {
    override suspend fun insertCard(card: CreditCard) {
        dao.insertCard(card)
    }

    override suspend fun deleteCard(card: CreditCard) {
        dao.deleteCard(card)
    }

    override suspend fun deleteCardById(cardId: Long) {
        dao.deleteCardById(cardId)
    }

    override suspend fun getCardById(cardId: Long): CreditCard {
        return dao.getCardById(cardId)
    }

    override fun getUserCards(userId: Long): Flow<List<CardInfo>> {
        return dao.getAllCards(userId)
    }

    override fun getUserCardsCursor(userId: Long): Cursor {
        return dao.getAllCardsCursor(userId)
    }

    override suspend fun getUserBackup(userId: Long): List<CreditCardBackupDto> {
        return dao.getUserBackup(userId)
    }
}