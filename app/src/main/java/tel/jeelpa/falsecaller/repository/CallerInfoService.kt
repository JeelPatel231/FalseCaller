package tel.jeelpa.falsecaller.repository

import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import tel.jeelpa.falsecaller.models.CallerInfo

interface CallerInfoService {
    suspend fun getDetailsFromNumber(number: PhoneNumber): CallerInfo
}
