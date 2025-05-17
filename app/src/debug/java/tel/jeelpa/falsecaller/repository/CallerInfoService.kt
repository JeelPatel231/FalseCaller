package tel.jeelpa.falsecaller.repository

import tel.jeelpa.falsecaller.models.CallerInfo
import tel.jeelpa.falsecaller.models.PhoneNumber

class MockCallerInfoService: CallerInfoService {
    override suspend fun getDetailsFromNumber(number: PhoneNumber): CallerInfo {
        return CallerInfo(
            name = "John Doe",
            spamScore = 0.0,
            score = 0.0,
        )
    }
}