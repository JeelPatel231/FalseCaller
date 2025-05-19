package tel.jeelpa.falsecaller.repository

import android.content.Context
import android.provider.CallLog
import tel.jeelpa.falsecaller.models.CallLogEntry
import tel.jeelpa.falsecaller.paging.CursorToIterator
import tel.jeelpa.falsecaller.paging.core.Paginator
import tel.jeelpa.falsecaller.paging.core.paged
import tel.jeelpa.falsecaller.paging.core.suspendediterator.emptyPaginator
import tel.jeelpa.falsecaller.utils.PhoneNumberUtil

interface CallLogRepo {
    fun getAllCallLogs(): Paginator<CallLogEntry>
    fun filterCallLogsByNumber(number: String): Paginator<CallLogEntry>
}

class AndroidCallLogRepo(
    private val context: Context,
) : CallLogRepo {
    private val PAGE_SIZE = 100
    private fun getCallLogs(
        filter: String = "",
        filterArgs: Array<String> = emptyArray()
    ): Iterator<CallLogEntry> {
        val projection = arrayOf(
            CallLog.Calls.NUMBER,
//            CallLog.Calls.DATE,
//            CallLog.Calls.DURATION,
//            CallLog.Calls.TYPE,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.CACHED_PHOTO_URI,
        )

        // TODO: should filter only incoming calls.
        val sortOrder = "${CallLog.Calls.DATE} DESC"
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            filter,
            filterArgs,
            sortOrder,
        )!!
        return CursorToIterator(cursor) {
            val number = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
//                val date = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DATE))
//                val duration = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DURATION))
//                val type = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.TYPE))
            val name = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME))
            val url = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.CACHED_PHOTO_URI))
            // TODO: default region should be choosable in settings
            CallLogEntry(
                name = name,
                number = PhoneNumberUtil.parse(number, "IN"),
                avatarUri = url,
            )
        }
    }

    override fun getAllCallLogs(): Paginator<CallLogEntry> {
        return getCallLogs()
            .asSequence()
            .distinctBy { it.number }
            .iterator()
            .paged(PAGE_SIZE)
    }

    override fun filterCallLogsByNumber(number: String): Paginator<CallLogEntry> {
        val filterString = "${CallLog.Calls.NUMBER} LIKE ?"
        val filterArgs = arrayOf("%${number}%")
        return getCallLogs(filterString, filterArgs)
            .asSequence()
            .distinctBy { it.number }
            .iterator()
            .paged(PAGE_SIZE)
    }

}

object EmptyCallLogRepo: CallLogRepo {
    override fun getAllCallLogs(): Paginator<CallLogEntry> =
        emptyPaginator()

    override fun filterCallLogsByNumber(number: String): Paginator<CallLogEntry> =
        emptyPaginator()
}
