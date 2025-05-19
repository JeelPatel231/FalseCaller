package tel.jeelpa.falsecaller.utils

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil as PhoneNumberUtilClass
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import org.koin.core.context.GlobalContext


val PhoneNumberUtil
    get() = GlobalContext.get().get<PhoneNumberUtilClass>()

val PhoneNumber.e164Format: String
    get() = PhoneNumberUtil.format(this, PhoneNumberUtilClass.PhoneNumberFormat.E164)