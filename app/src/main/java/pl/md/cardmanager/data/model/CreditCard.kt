package pl.md.cardmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class CreditCard(
    val name: String,
    val number: String,
    val CVC: Int,
    val ownerName: String,
    val expirationDate: Date,
    var userId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long? = null
)
