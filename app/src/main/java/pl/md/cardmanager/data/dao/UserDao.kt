package pl.md.cardmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.md.cardmanager.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM User WHERE name = :userName AND password = :userPassword")
    suspend fun getUser(userName: String, userPassword: String): User?

    @Query("SELECT * FROM User WHERE id = :userId")
    suspend fun getUserById(userId: Long): User

    @Query("SELECT * FROM User WHERE name = :userName")
    suspend fun getUserByName(userName: String): User?

    @Query("SELECT incorrectLoginAttempt FROM User WHERE id = :userId")
    suspend fun getFailedAuthenticationAttemptsCount(userId: Long): Int

}