package tel.jeelpa.falsecaller.ui.screens.settings

import androidx.lifecycle.ViewModel
import tel.jeelpa.falsecaller.mvi.MVI
import tel.jeelpa.falsecaller.mvi.emitSideEffect
import tel.jeelpa.falsecaller.mvi.mvi
import tel.jeelpa.falsecaller.ui.screens.settings.SettingsContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.settings.SettingsContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.settings.SettingsContract.UiState

class SettingsScreenViewModel : ViewModel(),
    MVI<UiState, UiAction, SideEffect> by mvi(UiState.default()) {


    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            UiAction.NavigateToOtpTokenScreen -> emitSideEffect(SideEffect.NavigateToOtpTokenScreen)
            is UiAction.SetSystemAlertWindowPermission -> updateUiState {
                UiState.isSystemAlertWindowGranted.set(this, uiAction.allowed)
            }
            UiAction.NavigateToDrawOverOtherAppsPermissionWindow -> emitSideEffect(SideEffect.NavigateToDrawOverOtherAppsPermissionWindow)
            is UiAction.SetPhonePermission -> updateUiState {
                UiState.isPhonePermissionGranted.set(this, uiAction.allowed)
            }
        }
    }
}