package tel.jeelpa.falsecaller.paging.core

class FlatIteratorToPaged<T : Any>(
    private val iterator: Iterator<T>,
    private val pageSize: Int = 20,
) : Paginator<T> {

    private var firstPageReturned = false

    override suspend fun hasNext(): Boolean {
        // If this is the first call and the iterator is empty,
        // we still want to return one empty page
        return !firstPageReturned || iterator.hasNext()
    }

    override suspend fun next(): Page<T> {
        if (firstPageReturned && !iterator.hasNext()) {
            throw IndexOutOfBoundsException()
        }

        firstPageReturned = true

        val chunk = buildList {
            for(i in 0 until pageSize) {
                if (!iterator.hasNext()) break
                add(iterator.next())
            }
        }

        return Page(chunk)
    }
}

fun <T: Any> Iterator<T>.paged(size: Int): Paginator<T> = FlatIteratorToPaged(this, size)