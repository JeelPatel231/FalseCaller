package tel.jeelpa.falsecaller.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallLogEntry(
    val name: String,
    val number: String,
    val avatarUri: String?,
): Parcelable
