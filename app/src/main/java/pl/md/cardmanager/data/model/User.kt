package pl.md.cardmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class User(
    val name: String,
    val password: String,
    var incorrectLoginAttempt: Int,
    val pin: String,
    @PrimaryKey(autoGenerate = true) val id: Long? = null
)