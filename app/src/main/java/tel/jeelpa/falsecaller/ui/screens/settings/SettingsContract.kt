package tel.jeelpa.falsecaller.ui.screens.settings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import arrow.optics.optics

interface SettingsContract {
    @optics
    @Stable
    @Immutable
    data class UiState(
        val isSystemAlertWindowGranted: Boolean,
        val isPhonePermissionGranted: Boolean,
    ) {
        companion object {
            fun default() : UiState {
                return UiState(
                    isSystemAlertWindowGranted = false,
                    isPhonePermissionGranted = false,
                )
            }
        }
    }

    sealed interface UiAction {
        data class SetSystemAlertWindowPermission(val allowed: Boolean): UiAction
        data class SetPhonePermission(val allowed: Boolean): UiAction
        data object NavigateToDrawOverOtherAppsPermissionWindow: UiAction
    }

    sealed interface SideEffect {
        data class Toast(val text: String): SideEffect
        data object NavigateToDrawOverOtherAppsPermissionWindow: SideEffect
    }
}