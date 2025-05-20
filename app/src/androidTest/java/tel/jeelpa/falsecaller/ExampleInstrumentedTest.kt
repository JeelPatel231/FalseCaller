package tel.jeelpa.falsecaller

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.test.runTest
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import tel.jeelpa.falsecaller.repository.room.AppDatabase
import tel.jeelpa.falsecaller.repository.room.CallerDao
import tel.jeelpa.falsecaller.repository.room.CallerEntity
import tel.jeelpa.falsecaller.repository.room.PhoneNumberConverter
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var callerDao: CallerDao
    private lateinit var db: AppDatabase
    private lateinit var phoneNumberUtil: PhoneNumberUtil

    @Before
    fun createDb() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        phoneNumberUtil = PhoneNumberUtil.createInstance(appContext)
        val convertor = PhoneNumberConverter(phoneNumberUtil = phoneNumberUtil)
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
            .addTypeConverter(convertor)
            .build()
        callerDao = db.callerDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("tel.jeelpa.falsecaller", appContext.packageName)
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runTest {
        val number = phoneNumberUtil.parse("+15555555555", "US")
        val number2 = phoneNumberUtil.parse("+15555555555", "IN")
        val caller = CallerEntity(
            number = number,
            score = 0.0,
            spamScore = 0.0,
            name = "John Doe"
        )
        callerDao.upsertAll(caller)
        val byNumber = callerDao.findByNumbers(number).single()
        val byNumber2 = callerDao.findByNumbers(number2).single()
        assertEquals(caller.number, byNumber.number)
        assertEquals(number, number2)
        assertEquals(byNumber.number, byNumber2.number)
    }

    @Test
    @Throws(Exception::class)
    fun upsertTest() = runTest {
        val number = phoneNumberUtil.parse("+15555555555", "US")
        val caller = CallerEntity(
            number = number,
            score = 0.0,
            name = "John Doe",
            spamScore = 0.0,
        )
        callerDao.upsertAll(caller)
        val callerNew = CallerEntity(
            number = number,
            score = 1.0,
            spamScore = 1.0,
            name = "Doe John",
        )
        callerDao.upsertAll(callerNew)

        val fetched = callerDao.findByNumbers(number).single()
        assertEquals(fetched.name, "Doe John")
        val singleEntry = callerDao.getAll().single()
        assertEquals(singleEntry.name, "Doe John")
    }
}