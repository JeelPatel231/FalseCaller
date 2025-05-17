package tel.jeelpa.falsecaller.ui.screens.truecaller

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import arrow.optics.optics

interface TrueCallerOtpContract {
    @optics
    @Stable
    @Immutable
    data class UiState(
        val number: String,
        val otp: String,
        val sendOtpResponse: SendOtpResponse? = null,
    ) {
        companion object {
            fun default() = UiState(
                number = "",
                otp = "",
                sendOtpResponse = null
            )
        }
    }

    sealed interface UiAction {
        data class UpdateNumber(val number: String): UiAction
        data class UpdateOtp(val otp: String): UiAction
        data class SendOtp(val number: String): UiAction
        data class VerifyOtp(val plainNumber: String, val otp: String, val sendOtpResponse: SendOtpResponse): UiAction
    }

    sealed interface SideEffect {
        data class Toast(val text: String): SideEffect
        data object NavigateBack: SideEffect
    }
}