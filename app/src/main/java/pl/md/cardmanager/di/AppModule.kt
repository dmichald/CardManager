package pl.md.cardmanager.di

import android.app.Application
import android.util.Base64
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import pl.md.cardmanager.data.CardDatabase
import pl.md.cardmanager.data.repository.CreditCardRepository
import pl.md.cardmanager.data.repository.CreditCardRepositoryImpl
import pl.md.cardmanager.data.repository.UserRepository
import pl.md.cardmanager.data.repository.UserRepositoryImpl
import pl.md.cardmanager.util.SharedPreferencesUtils
import pl.md.cardmanager.util.UserUtils
import pl.md.cardmanager.util.crypto.BcryptUtil
import pl.md.cardmanager.util.crypto.Decryptor
import pl.md.cardmanager.util.crypto.EnCryptor
import pl.md.cardmanager.util.crypto.KeyStoreUtil
import javax.inject.Singleton

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
        val isFirstRun = SharedPreferencesUtils.getBoolean(
            app.applicationContext,
            UserUtils.FIRST_RUN_SHARED_PREF
        )
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
            passPhrase = BcryptUtil.secureRandom()
            val key = KeyStoreUtil.generateAndSave(UserUtils.DB_KEY_SHARED_PREF)
            val encrypted = EnCryptor.encryptText(key, passPhrase)
            val encryptionAsString = Base64.encodeToString(encrypted.encryption, Base64.NO_WRAP)
            SharedPreferencesUtils.putString(
                app.applicationContext,
                UserUtils.DB_KEY_SHARED_PREF,
                encryptionAsString
            )
            val encryptionIvAsString = Base64.encodeToString(encrypted.encryptionIV, Base64.NO_WRAP)
            SharedPreferencesUtils.putString(
                app.applicationContext,
                UserUtils.DB_KEY_IV_SHARED_PREF,
                encryptionIvAsString
            )
            return passPhrase
        } else {
            val encryptedPassword = SharedPreferencesUtils.getString(
                app.applicationContext,
                UserUtils.DB_KEY_SHARED_PREF
            )
            val encrptionIV = SharedPreferencesUtils.getString(
                app.applicationContext,
                UserUtils.DB_KEY_IV_SHARED_PREF
            )
            passPhrase = Decryptor.decryptData(
                UserUtils.DB_KEY_SHARED_PREF,
                Base64.decode(encryptedPassword, Base64.NO_WRAP),
                Base64.decode(encrptionIV, Base64.NO_WRAP)
            )
            return passPhrase
        }
    }
}