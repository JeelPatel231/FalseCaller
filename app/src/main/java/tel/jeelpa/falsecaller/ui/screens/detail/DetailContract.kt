package tel.jeelpa.falsecaller.ui.screens.detail

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import arrow.optics.optics
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber

interface DetailContract {
    @optics
    @Stable
    @Immutable
    data class UiState(
        val phoneNumber: PhoneNumber,
    ) {
        companion object
    }

    sealed interface UiAction {
    }

    sealed interface SideEffect {
        data class Toast(val text: String): SideEffect
    }
}