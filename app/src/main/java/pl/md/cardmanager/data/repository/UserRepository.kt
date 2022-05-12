package pl.md.cardmanager.data.repository

import pl.md.cardmanager.data.model.User
import pl.md.cardmanager.ui.model.UserRegisterDto

interface UserRepository {
    suspend fun insertUser(newUser: UserRegisterDto): User
    suspend fun getUser(userName: String, userPassword: String): User?
    suspend fun getUserById(userId: Long): User
    suspend fun getUserByName(userName: String): User?
    suspend fun addFailedLoginAttempt(userId: Long)
    suspend fun getFailedAuthenticationAttemptsCount(userId: Long): Int
    suspend fun resetFailedLoginAttempts(userId: Long)
    suspend fun insertUserEncryptedPassword(userId: Long, encryptedId: String)
}