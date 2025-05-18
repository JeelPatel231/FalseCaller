package tel.jeelpa.falsecaller.ui.screens.floatingwindow

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import arrow.optics.optics
import tel.jeelpa.falsecaller.models.PhoneNumber

interface FloatingWindowContract {
    @optics
    @Stable
    @Immutable
    data class UiState(
        val number: PhoneNumber,
    ) {
        companion object
    }

    sealed interface UiAction {
    }

    sealed interface SideEffect {
        data class Toast(val text: String): SideEffect
    }
}