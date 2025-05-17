package tel.jeelpa.falsecaller.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

// automatically get CoroutineScope within ViewModels
fun <UiState, UiAction, SideEffect, MviViewModel> MviViewModel.emitSideEffect(sideEffect: SideEffect)
        where
        MviViewModel: MVI<UiState, UiAction, SideEffect>,
        MviViewModel: ViewModel
{
    viewModelScope.emitSideEffect(sideEffect)
}

