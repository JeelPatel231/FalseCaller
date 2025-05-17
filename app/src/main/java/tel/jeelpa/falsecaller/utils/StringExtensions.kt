package tel.jeelpa.falsecaller.utils

fun String?.nullIfBlank(): String? {
    if (this.isNullOrBlank()) return null
    return this
}