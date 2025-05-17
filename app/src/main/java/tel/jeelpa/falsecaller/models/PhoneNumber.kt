package tel.jeelpa.falsecaller.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class PhoneNumber(val number: String): Parcelable {
//    init {
//        require(number.startsWith("+")) { "Number must start with Country code. eg: +911234567890. Input was $number" }
//        val regex = Regex("\\+[0-9]+")
//        require(number.matches(regex)) { "Number must only contain + and Numbers. Input was $number" }
//    }
    companion object {
        fun parse(number: String): PhoneNumber {
            return PhoneNumber(number.filter { it == '+' || it.isDigit() })
        }
    }
}
