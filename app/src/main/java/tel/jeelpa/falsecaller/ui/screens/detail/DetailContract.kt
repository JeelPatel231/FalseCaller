package tel.jeelpa.falsecaller.ui.screens.detail

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.paging.Pager
import arrow.optics.optics
import tel.jeelpa.falsecaller.models.CallLogEntry
import tel.jeelpa.falsecaller.models.PhoneNumber
import tel.jeelpa.falsecaller.paging.core.suspendediterator.emptyPaginator
import tel.jeelpa.falsecaller.paging.getPager

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