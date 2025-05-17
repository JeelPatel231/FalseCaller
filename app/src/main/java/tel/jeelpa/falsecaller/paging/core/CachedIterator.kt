package tel.jeelpa.falsecaller.paging.core

import tel.jeelpa.falsecaller.paging.core.suspendediterator.SuspendIterator

class CachingIterator<T : Any>(private val iterator: Iterator<T>) {
    private val collectedElements: MutableList<T> = mutableListOf()

    private val _lastCollectedElement
        get() = collectedElements.size

    operator fun get(index: Int): T {
        if(index >= _lastCollectedElement) {
            // the page is not fetched yet. fetch the page and cache it
            val remainingElements = index - _lastCollectedElement

            for(i in 0..remainingElements) {
                if(!iterator.hasNext()) throw IndexOutOfBoundsException()

                collectedElements.add(iterator.next())
            }
        }

        return collectedElements[index]
    }

    fun hasNext(): Boolean = iterator.hasNext()
}

class CachingSuspendIterator<T : Any>(private val iterator: SuspendIterator<T>) {
    private val collectedElements: MutableList<T> = mutableListOf()

    private val _lastCollectedElement
        get() = collectedElements.size

    suspend fun get(index: Int): T {
        if(index >= _lastCollectedElement) {
            // the page is not fetched yet. fetch the page and cache it
            val remainingElements = index - _lastCollectedElement

            for(i in 0..remainingElements) {
                collectedElements.add(iterator.next())
            }
        }

        return collectedElements[index]
    }

    suspend fun hasNext(): Boolean = iterator.hasNext()
}


fun <T: Any> Iterator<T>.cached() = CachingIterator(this)
fun <T: Any> SuspendIterator<T>.cached() = CachingSuspendIterator(this)
