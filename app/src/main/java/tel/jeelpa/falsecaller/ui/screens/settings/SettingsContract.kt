package tel.jeelpa.falsecaller.ui.screens.settings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import arrow.optics.optics
import tel.jeelpa.falsecaller.models.CallLogEntry

interface SettingsContract {
    @Stable
    @Immutable
    data object UiState

    sealed interface UiAction {
    }

    sealed interface SideEffect {
        data class Toast(val text: String): SideEffect
    }
}