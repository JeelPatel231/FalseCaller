package tel.jeelpa.falsecaller.repository.room

import arrow.core.None
import arrow.core.Some
import arrow.core.toOption
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import tel.jeelpa.falsecaller.models.CallerInfo
import tel.jeelpa.falsecaller.models.CallerMapper
import tel.jeelpa.falsecaller.repository.CallerInfoService
import kotlin.time.Duration

val TTL = Duration.parse("1d").inWholeMilliseconds

class RoomCachedCallerInfoService(
    private val callerDao: CallerDao,
    private val remoteRepo: CallerInfoService,
): CallerInfoService {
    override suspend fun getDetailsFromNumber(number: PhoneNumber): CallerInfo {
        val now = System.currentTimeMillis()

        // if db entry non existent or out ofo date, fetch from remote.
        callerDao.findByNumbers(number)
            .singleOrNull()
            .toOption()
            .flatMap { if ((now - it.lastUpdated) > TTL) { None } else { Some(it) }  }
            .onSome {
                println("Entry was found in DB. Returning.")
            }
            .onNone {
                println("Entry was non existent or expired. Getting fresh value.")
                val latest = remoteRepo.getDetailsFromNumber(number)
                callerDao.upsertAll(CallerMapper.infoToEntity(latest))
            }

        // entry must exist and should only be 1
        val latest = callerDao.findByNumbers(number).single()
        return CallerMapper.entityToInfo(latest)
    }
}