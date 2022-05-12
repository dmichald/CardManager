package pl.md.cardmanager.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithCards(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val creditCards: List<CreditCard>
)
