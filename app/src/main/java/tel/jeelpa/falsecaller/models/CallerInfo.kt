package tel.jeelpa.falsecaller.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
@Stable
data class CallerInfo(
    val name: String,
    val spamScore: Double,
    val score: Double,
)