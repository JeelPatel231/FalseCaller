package tel.jeelpa.falsecaller.repository

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import tel.jeelpa.falsecaller.models.CallLogEntry
import tel.jeelpa.falsecaller.paging.core.Paginator
import tel.jeelpa.falsecaller.paging.core.paged

class MockCallLogRepo(
    private val phoneNumberUtil: PhoneNumberUtil
) : CallLogRepo {
    override fun getAllCallLogs(): Paginator<CallLogEntry> {
        return (0 until 10).map {
            CallLogEntry(
                number = phoneNumberUtil.getExampleNumber(PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY),
                name = "Person $it",
                avatarUri = "https://picsum.photos/50"
            )
        }
            .iterator().paged(10)
    }

    override fun filterCallLogsByNumber(number: String): Paginator<CallLogEntry> {
        return (0 until 10).map {
            CallLogEntry(
                number = phoneNumberUtil.getExampleNumber(PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY),
                name = "Person $it",
                avatarUri = "https://picsum.photos/200?random=$it"
            )
        }.iterator()
            .paged(10)
    }
}