package tel.jeelpa.falsecaller.ui.screens.floatingwindow

import androidx.lifecycle.ViewModel
import arrow.core.Either
import arrow.core.None
import arrow.core.some
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import tel.jeelpa.falsecaller.flow.deriveStateIn
import tel.jeelpa.falsecaller.flow.mapState
import tel.jeelpa.falsecaller.models.CallerInfo
import tel.jeelpa.falsecaller.models.PhoneNumber
import tel.jeelpa.falsecaller.models.TriState
import tel.jeelpa.falsecaller.mvi.MVI
import tel.jeelpa.falsecaller.mvi.mvi
import tel.jeelpa.falsecaller.repository.CallerInfoService
import tel.jeelpa.falsecaller.ui.screens.floatingwindow.FloatingWindowContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.floatingwindow.FloatingWindowContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.floatingwindow.FloatingWindowContract.UiState
import tel.jeelpa.falsecaller.utils.logCatch

class FloatingWindowViewModel(
    number: PhoneNumber,
    private val callerInfoService: CallerInfoService
): ViewModel(), MVI<UiState, UiAction, SideEffect> by mvi(UiState(number)) {

    val detailsFromTruecaller: StateFlow<TriState<CallerInfo>> = uiState
        .mapState { it.number }
        .map {
            Either.logCatch { callerInfoService.getDetailsFromNumber(it) }
                .some()
        }
        .deriveStateIn { None }

}