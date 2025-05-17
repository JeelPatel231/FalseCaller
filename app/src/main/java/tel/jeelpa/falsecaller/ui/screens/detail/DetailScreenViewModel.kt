package tel.jeelpa.falsecaller.ui.screens.detail

import androidx.lifecycle.ViewModel
import arrow.core.Either
import arrow.core.None
import arrow.core.some
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import tel.jeelpa.falsecaller.flow.deriveStateIn
import tel.jeelpa.falsecaller.flow.mapState
import tel.jeelpa.falsecaller.models.CallLogEntry
import tel.jeelpa.falsecaller.models.CallerInfo
import tel.jeelpa.falsecaller.models.TriState
import tel.jeelpa.falsecaller.mvi.MVI
import tel.jeelpa.falsecaller.mvi.emitSideEffect
import tel.jeelpa.falsecaller.mvi.mvi
import tel.jeelpa.falsecaller.repository.CallerInfoService
import tel.jeelpa.falsecaller.ui.screens.detail.DetailContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.detail.DetailContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.detail.DetailContract.UiState

class DetailScreenViewModel(
    callLogEntry: CallLogEntry,
    private val callerInfoService: CallerInfoService,
) : ViewModel(),
    MVI<UiState, UiAction, SideEffect> by mvi(UiState(callLogEntry)) {

    val detailsFromTruecaller: StateFlow<TriState<CallerInfo>> = uiState
        .mapState { it.logEntry }
        .map {
            Either.catch { callerInfoService.getDetailsFromNumber(it.number) }
                .onLeft { emitSideEffect(SideEffect.Toast(it.message ?: "Unknown Error")) }
                .some()
        }
        .deriveStateIn { None }


    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            else -> error("Unreachable $uiAction")
        }
    }
}