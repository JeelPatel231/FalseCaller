package tel.jeelpa.falsecaller.paging.core.suspendediterator

internal class SuspendTransformingSequence<T, R>(
    private val iterator: SuspendIterator<T>,
    private val transformer: (T) -> R
) : SuspendIterator<R> {
    override suspend fun next(): R {
        return transformer(iterator.next())
    }

    override suspend fun hasNext(): Boolean {
        return iterator.hasNext()
    }
}

fun <T, R> SuspendIterator<T>.map(
    transformer: (T) -> R
): SuspendIterator<R> =
    SuspendTransformingSequence(this, transformer)