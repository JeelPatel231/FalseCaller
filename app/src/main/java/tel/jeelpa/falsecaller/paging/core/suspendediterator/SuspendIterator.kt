package tel.jeelpa.falsecaller.paging.core.suspendediterator

import tel.jeelpa.falsecaller.paging.core.Page
import tel.jeelpa.falsecaller.paging.core.Paginator
import tel.jeelpa.falsecaller.paging.core.singlePageOf

/** Same as kotlin.collections.Iterator<T> but functions are suspended for network/IO tasks */
interface SuspendIterator<out T> {
    /**
     * Returns the next element in the iteration.
     *
     * @throws NoSuchElementException if the iteration has no next element.
     */
    suspend operator fun next(): T

    /**
     * Returns `true` if the iteration has more elements.
     */
    suspend operator fun hasNext(): Boolean
}

/**
 * This class is only supposed to be used for testing and tasks which don't do blocking IO
 * Using this class incorrectly can block the main thread and degrade app experience/performance
 **/
class SuspendIteratorFromSyncImpl<T>(private val iterator: Iterator<T>): SuspendIterator<T> {
    override suspend fun next(): T = iterator.next()
    override suspend fun hasNext(): Boolean = iterator.hasNext()
}

fun <T> Iterable<T>.suspendIterator(): SuspendIterator<T> = SuspendIteratorFromSyncImpl(this.iterator())

fun <T> Iterator<T>.toSuspendIterator(): SuspendIterator<T> = SuspendIteratorFromSyncImpl(this)

fun <T : Any> emptyPaginator(): Paginator<T> = listOf(Page(emptyList<T>())).suspendIterator()