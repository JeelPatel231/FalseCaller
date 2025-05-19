package tel.jeelpa.falsecaller.models

import android.os.Parcelable
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallLogEntry(
    val name: String,
    val number: PhoneNumber,
    val avatarUri: String?,
): Parcelable
