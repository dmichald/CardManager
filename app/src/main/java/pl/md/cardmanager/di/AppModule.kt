package pl.md.cardmanager.di

import android.app.Application
import android.util.Base64
import android.util.Log
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import pl.md.cardmanager.CardApp
import pl.md.cardmanager.data.CardDatabase
import pl.md.cardmanager.data.repository.CreditCardRepository
import pl.md.cardmanager.data.repository.CreditCardRepositoryImpl
import pl.md.cardmanager.data.repository.UserRepository
import pl.md.cardmanager.data.repository.UserRepositoryImpl
import pl.md.cardmanager.util.UserUtils
import pl.md.cardmanager.util.crypto.Decryptor
import pl.md.cardmanager.util.crypto.EnCryptor
import pl.md.cardmanager.util.crypto.KeyStoreUtil
import java.nio.charset.Charset
import java.security.SecureRandom
import javax.inject.Singleton
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    val TAG = AppModule::class.qualifiedName

    @Provides
    @Singleton
    fun provideCardDatabase(app: Application): CardDatabase {
        val builder = Room.databaseBuilder(
            app,
            CardDatabase::class.java,
            "card.db"
        )
        val isFirstRun = UserUtils.getBoolean(app.applicationContext, UserUtils.FIRST_RUN)
        val passPhrase = getPassword(isFirstRun, app)
        val factory = SupportFactory(passPhrase.toByteArray())
        builder.openHelperFactory(factory)

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideUserRepository(db: CardDatabase): UserRepository {
        return UserRepositoryImpl(db.userDao)
    }

    @Provides
    @Singleton
    fun provideCreditCardRepository(db: CardDatabase): CreditCardRepository {
        return CreditCardRepositoryImpl(db.creditCardDao)
    }

    fun getPassword(isFirstRun: Boolean, app: Application): String {
        val passPhrase: String
        if (isFirstRun) {
            passPhrase = Random.nextInt(10000,9999999).toString()
            val key = KeyStoreUtil.generateAndSave(UserUtils.DB_KEY)
            val encrypted = EnCryptor.encryptText(key, passPhrase)
            val encryptionAsString = Base64.encodeToString(encrypted.encryption, Base64.NO_WRAP)
            UserUtils.putString(
                app.applicationContext,
                UserUtils.DB_KEY,
                encryptionAsString
            )
            val encryptionIvAsString = Base64.encodeToString(encrypted.encryptionIV, Base64.NO_WRAP)
            UserUtils.putString(
                app.applicationContext,
                UserUtils.DB_KEY_IV,
                encryptionIvAsString
            )
            return passPhrase
        } else {
            val encryptedPassword = UserUtils.getString(app.applicationContext, UserUtils.DB_KEY)
            val encrptionIV = UserUtils.getString(app.applicationContext, UserUtils.DB_KEY_IV)
            passPhrase = Decryptor.decryptData(
                UserUtils.DB_KEY,
                Base64.decode(encryptedPassword, Base64.NO_WRAP),
                Base64.decode(encrptionIV, Base64.NO_WRAP)
            )
            return passPhrase
        }
    }
}