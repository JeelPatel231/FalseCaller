package tel.jeelpa.falsecaller.paging.core

import tel.jeelpa.falsecaller.paging.core.suspendediterator.SuspendIterator

@JvmInline value class Page<T: Any>(val items: List<T>)

typealias Paginator<TData> = SuspendIterator<Page<TData>>

fun <E : Any> singlePageOf(items: Collection<E>): List<Page<E>> {
    return listOf(Page(items.toList()))
}
