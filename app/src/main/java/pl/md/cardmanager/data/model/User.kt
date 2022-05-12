package pl.md.cardmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class User(
    val name: String,
    val password: String,
    var incorrectLoginAttempt: Int,
    val pin: String,
    var encryptedUserId: String = "",
    val userKeyAlias: String = UUID.randomUUID().toString(),
    @PrimaryKey(autoGenerate = true) val id: Long? = null
)