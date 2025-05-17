package tel.jeelpa.falsecaller.paging.core.suspendediterator

/** Same implementation as Iterator<T>.toList() but the calls are suspended */
suspend fun <T : Any> SuspendIterator<T>.toList(): List<T> {
    if (!hasNext())
        return emptyList()
    val element = next()
    if (!hasNext())
        return listOf(element)
    val dst = ArrayList<T>()
    dst.add(element)
    while (hasNext()) dst.add(next())
    return dst
}
