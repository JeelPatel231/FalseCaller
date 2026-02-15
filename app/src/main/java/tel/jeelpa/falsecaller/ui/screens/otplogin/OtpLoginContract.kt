package tel.jeelpa.falsecaller.ui.screens.otplogin

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import arrow.optics.optics

interface OtpLoginContract {
    enum class Step {
        PhoneNumber,
        Otp,
        Verification
    }

    @optics
    @Stable
    @Immutable
    data class UiState(
        val number: String,
        val otp: String,
        val step: Step,
        val sendOtpResponse: SendOtpResponse?,
    ) {
        companion object {
            fun default() : UiState {
                return UiState(
                    number = "",
                    otp = "",
                    step = Step.PhoneNumber,
                    sendOtpResponse = null,
                )
            }
        }
    }

    sealed interface UiAction {
        data class PhoneNumberFieldChanged(val number: String): UiAction
        data class OtpFieldChanged(val otp: String): UiAction
        data class SendOtpToNumber(val number: String): UiAction

        data class VerifyOtp(val number: String, val otp: String, val sendOtpResponse: SendOtpResponse): UiAction
    }

    sealed interface SideEffect {
        data class Toast(val text: String): SideEffect
        data class StoreTokenInSharedPrefs(val token: String): SideEffect
    }
}