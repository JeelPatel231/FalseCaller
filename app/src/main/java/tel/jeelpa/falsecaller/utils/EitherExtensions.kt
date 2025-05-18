package tel.jeelpa.falsecaller.utils

import arrow.core.Either

inline fun <R> Either.Companion.logCatch(f: () -> R) = catch(f).onLeft { it.printStackTrace() }