package tel.jeelpa.falsecaller.repository

import tel.jeelpa.falsecaller.models.CallerInfo
import tel.jeelpa.falsecaller.models.PhoneNumber

interface CallerInfoService {
    suspend fun getDetailsFromNumber(number: PhoneNumber): CallerInfo
}
