package tel.jeelpa.falsecaller.repository

import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import tel.jeelpa.falsecaller.models.CallerInfo

class MockCallerInfoService: CallerInfoService {
    override suspend fun getDetailsFromNumber(number: PhoneNumber): CallerInfo {
        return CallerInfo(
            name = "John Doe",
            spamScore = 0.0,
            score = 0.0,
            number = number,
        )
    }
}