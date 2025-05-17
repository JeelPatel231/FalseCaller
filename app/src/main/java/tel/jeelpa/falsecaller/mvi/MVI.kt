package tel.jeelpa.falsecaller.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MVI<UiState, UiAction, SideEffect> {
    val uiState: StateFlow<UiState>
    val sideEffect: Flow<SideEffect>

    fun onAction(uiAction: UiAction)

    fun updateUiState(block: UiState.() -> UiState)

    fun CoroutineScope.emitSideEffect(effect: SideEffect)
}

fun <UiState, UiAction, SideEffect> mvi(
    initialUiState: UiState,
): MVI<UiState, UiAction, SideEffect> = MVIDelegate(initialUiState)

