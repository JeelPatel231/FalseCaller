package tel.jeelpa.falsecaller.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CallerEntity::class], version = 1)
@TypeConverters(PhoneNumberConverter::class)
abstract class AppDatabase : RoomDatabase() {
    protected abstract fun _innerCallerDao(): CallerDao

    fun callerDao() = WrappedCallerDao(_innerCallerDao())
}