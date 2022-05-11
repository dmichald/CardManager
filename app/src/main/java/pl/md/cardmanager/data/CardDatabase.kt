package pl.md.cardmanager.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.md.cardmanager.data.converters.DateConverter
import pl.md.cardmanager.data.dao.CreditCardDao
import pl.md.cardmanager.data.dao.UserDao
import pl.md.cardmanager.data.model.CreditCard
import pl.md.cardmanager.data.model.User

@Database(
    entities = [User::class, CreditCard::class],
    version = 3
)
@TypeConverters(DateConverter::class)
abstract class CardDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val creditCardDao: CreditCardDao
}