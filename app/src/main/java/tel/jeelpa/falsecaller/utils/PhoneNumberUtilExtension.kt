package tel.jeelpa.falsecaller.utils

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber

fun PhoneNumberUtil.e164Format(phoneNumber: PhoneNumber): String = format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)