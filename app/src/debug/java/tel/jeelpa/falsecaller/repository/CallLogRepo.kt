package tel.jeelpa.falsecaller.repository

import tel.jeelpa.falsecaller.models.CallLogEntry
import tel.jeelpa.falsecaller.models.PhoneNumber
import tel.jeelpa.falsecaller.paging.core.Paginator
import tel.jeelpa.falsecaller.paging.core.paged

class MockCallLogRepo : CallLogRepo {
    override fun getAllCallLogs(): Paginator<CallLogEntry> {
        return (0 until 10).map {
            CallLogEntry(
                number = PhoneNumber.parse("+1$it"),
                name = "Person $it",
                avatarUri = "https://picsum.photos/50"
            )
        }
            .iterator().paged(10)
    }

    override fun filterCallLogsByNumber(number: PhoneNumber): Paginator<CallLogEntry> {
        return (0 until 10).map {
            CallLogEntry(
                number = number,
                name = "Person $it",
                avatarUri = "https://picsum.photos/200?random=$it"
            )
        }.iterator()
            .paged(10)
    }
}