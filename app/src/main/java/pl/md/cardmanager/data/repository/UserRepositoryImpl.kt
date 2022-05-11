package pl.md.cardmanager.data.repository

import android.util.Log
import pl.md.cardmanager.data.dao.UserDao
import pl.md.cardmanager.data.model.User
import pl.md.cardmanager.ui.model.UserRegisterDto

class UserRepositoryImpl(private val dao: UserDao) : UserRepository {
    companion object {
        val TAG = UserRepositoryImpl::class.qualifiedName
    }

    override suspend fun insertUser(newUser: UserRegisterDto): Long {
        val user = User(newUser.username, newUser.password, 0, newUser.pin)
        val userId = dao.insertUser(user)
        Log.d(TAG, "Saved user with name: '${newUser.username}' + $user")
        return userId
    }

    override suspend fun getUser(userName: String, userPassword: String): User? {
        return dao.getUser(userName, userPassword)
    }

    override suspend fun getUserById(userId: Long): User {
        return dao.getUserById(userId)
    }

    override suspend fun getUserByName(userName: String): User? {
        return dao.getUserByName(userName)
    }

    override suspend fun getFailedAuthenticationAttemptsCount(userId: Long): Int {
        return dao.getFailedAuthenticationAttemptsCount(userId)
    }

    override suspend fun resetFailedLoginAttempts(userId: Long) {
        val user = dao.getUserById(userId)
        user.incorrectLoginAttempt = 0
        dao.insertUser(user)
    }

    override suspend fun addFailedLoginAttempt(userId: Long) {
        val user = dao.getUserById(userId)
        user.incorrectLoginAttempt = user.incorrectLoginAttempt + 1
        dao.insertUser(user)
    }
}