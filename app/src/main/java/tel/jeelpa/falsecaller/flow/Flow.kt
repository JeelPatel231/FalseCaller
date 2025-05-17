package tel.jeelpa.falsecaller.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun <X> Flow<X>.forceStateIn(): StateFlow<X> = deriveStateIn { runBlocking { first() } }

fun <X> Flow<X>.deriveStateIn(initialValue: () -> X): StateFlow<X> {
    return DerivedStateFlow(initialValue, this)
}
