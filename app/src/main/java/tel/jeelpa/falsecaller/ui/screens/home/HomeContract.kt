package tel.jeelpa.falsecaller.ui.screens.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import arrow.optics.optics
import tel.jeelpa.falsecaller.models.CallLogEntry

interface HomeContract {
    @optics
    @Stable
    @Immutable
    data class UiState(
        val searchQuery: String,
        val isSearchBarExpanded: Boolean,
        val isCallLogPermissionGranted: Boolean,
    ) {
        companion object {
            fun default() = UiState(
                searchQuery = "",
                isSearchBarExpanded = false,
                isCallLogPermissionGranted = false,
            )
        }
    }

    sealed interface UiAction {
        data class OnQueryChange(val newQuery: String): UiAction
        data class SetExpanded(val expanded: Boolean): UiAction
        data class OnCallLogItemClicked(val entry: CallLogEntry): UiAction
        data object OnSettingsIconClicked: UiAction
        data class OnPermissionChange(val accepted: Boolean): UiAction
    }

    sealed interface SideEffect {
        data class Toast(val text: String): SideEffect
        data class NavigateToDetails(val data: CallLogEntry): SideEffect
        data object NavigateToSetting: SideEffect
    }
}