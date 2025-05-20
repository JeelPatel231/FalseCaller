package tel.jeelpa.falsecaller.repository.room

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Upsert
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import tel.jeelpa.falsecaller.utils.e164Format

@Entity(tableName = "caller_entity")
data class CallerEntity(
    // these are read only, setting them wont do anything. they will be overwritten
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_accessed") val lastAccessed: Long = System.currentTimeMillis(),

    @PrimaryKey @ColumnInfo(name = "number") val number: PhoneNumber,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "spam_score") val spamScore: Double,
    @ColumnInfo(name = "score") val score: Double,
)

@ProvidedTypeConverter
class PhoneNumberConverter(
    private val phoneNumberUtil: PhoneNumberUtil
) {
    @TypeConverter
    fun fromPhoneNumber(phoneNumber: PhoneNumber): String {
        return phoneNumberUtil.e164Format(phoneNumber)
    }

    @TypeConverter
    fun toPhoneNumber(phoneNumber: String): PhoneNumber {
        // Default region will never be used in this case.
        return phoneNumberUtil.parse(phoneNumber, PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY)
    }
}

@Dao
interface CallerDao {
    @Query("SELECT * FROM caller_entity")
    suspend fun getAll(): List<CallerEntity>

    @Query("SELECT * FROM caller_entity WHERE number IN (:numbers)")
    suspend fun findByNumbers(vararg numbers: PhoneNumber): List<CallerEntity>

    @Upsert
    suspend fun upsertAll(vararg callerEntity: CallerEntity)

    @Delete
    suspend fun delete(callerEntity: CallerEntity)
}

// This is a decorator class which handles updating timestamps when inserting or accessing entries.
class WrappedCallerDao(
    private val callerDao: CallerDao
): CallerDao by callerDao {

    override suspend fun findByNumbers(vararg numbers: PhoneNumber): List<CallerEntity> {
        val currTime = System.currentTimeMillis()
        val found = callerDao.findByNumbers(*numbers).map { it.copy(lastAccessed = currTime) }
        callerDao.upsertAll(*found.toTypedArray())
        return found
    }

    override suspend fun upsertAll(vararg callerEntity: CallerEntity) {
        val currTime = System.currentTimeMillis()
        // update every entry's last accessed and updated time.
        val allUpdated = (callerEntity.map { it.copy(lastAccessed = currTime, lastUpdated = currTime) }).toTypedArray()
        callerDao.upsertAll(*allUpdated)
    }

}

