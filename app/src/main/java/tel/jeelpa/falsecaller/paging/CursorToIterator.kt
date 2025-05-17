package tel.jeelpa.falsecaller.paging

import android.database.Cursor

class CursorToIterator<T>(
    private val cursor: Cursor,
    private val mapper: (Cursor) -> T
) : Iterator<T> {
    private val lastIndex = cursor.count - 1

    override fun hasNext(): Boolean {
        if (cursor.isClosed) return false
        val hasNext = cursor.count > 0 && cursor.position < lastIndex
        return hasNext
    }

    override fun next(): T {
        cursor.moveToNext()
        val entry = mapper(cursor)
        if (!hasNext()) {
            // close the cursor if all the elements are used.
            // its fine if you close an already closed cursor.
            cursor.close()
        }

        return entry
    }
}