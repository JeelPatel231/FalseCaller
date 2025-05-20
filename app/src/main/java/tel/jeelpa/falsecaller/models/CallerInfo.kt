package tel.jeelpa.falsecaller.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import tel.jeelpa.falsecaller.repository.room.CallerEntity

@Immutable
@Stable
data class CallerInfo(
    val name: String,
    val spamScore: Double,
    val score: Double,
    val number: PhoneNumber,
)


object CallerMapper {
    fun infoToEntity(callerInfo: CallerInfo) = CallerEntity(
        name = callerInfo.name,
        score = callerInfo.score,
        spamScore = callerInfo.spamScore,
        number = callerInfo.number
    )

    fun entityToInfo(callerEntity: CallerEntity) = CallerInfo(
        name = callerEntity.name,
        score = callerEntity.score,
        spamScore = callerEntity.spamScore,
        number = callerEntity.number
    )
}