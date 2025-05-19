package tel.jeelpa.falsecaller.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import arrow.core.Either
import arrow.core.Option
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import tel.jeelpa.falsecaller.flow.mapState
import tel.jeelpa.falsecaller.models.CallLogEntry
import tel.jeelpa.falsecaller.mvi.MVI
import tel.jeelpa.falsecaller.mvi.emitSideEffect
import tel.jeelpa.falsecaller.mvi.mvi
import tel.jeelpa.falsecaller.paging.getPager
import tel.jeelpa.falsecaller.repository.CallLogRepo
import tel.jeelpa.falsecaller.ui.screens.home.HomeContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.home.HomeContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.home.HomeContract.UiState
import tel.jeelpa.falsecaller.utils.e164Format

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModel(
    private val androidCallLogRepo: CallLogRepo,
    private val emptyCallLogRepo: CallLogRepo,
    private val phoneNumberUtil: PhoneNumberUtil,
) : ViewModel(),
    MVI<UiState, UiAction, SideEffect> by mvi(UiState.default()) {

    private fun getCallLogRepo(uiState: UiState): CallLogRepo {
        return when (uiState.isCallLogPermissionGranted) {
            true -> androidCallLogRepo
            false -> emptyCallLogRepo
        }
    }

    val callLogs: Flow<PagingData<CallLogEntry>> = uiState
        .mapState {
            val repo = getCallLogRepo(it)
            getPager(repo.getAllCallLogs())
        }
        .flatMapLatest { it.flow }
        .cachedIn(viewModelScope)

    val intermediateCallLogEntry: StateFlow<Option<CallLogEntry>> = uiState.mapState {
        // TODO: REGION CODE should be choosable in settings
        Either
            .catch { phoneNumberUtil.parse(it.searchQuery, "IN") }
            .map { phoneNumberUtil.e164Format(it) }
            .map {
                CallLogEntry(
                    name = it,
                    number = it,
                    avatarUri = null,
                )
            }
            .getOrNone()
    }

    val searchResults: Flow<PagingData<CallLogEntry>> = uiState.mapState {
        val repo = getCallLogRepo(it)
        val filters = repo.filterCallLogsByNumber(it.searchQuery)
        getPager(filters)
    }
        .flatMapLatest { it.flow }
        .cachedIn(viewModelScope)

    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            // TODO: REGION CODE should be choosable in settings
            is UiAction.OnCallLogItemClicked -> emitSideEffect(SideEffect.NavigateToDetails(phoneNumberUtil.parse(uiAction.entry.number, "IN")))
            is UiAction.OnQueryChange -> updateUiState {
                UiState.searchQuery.set(this, uiAction.newQuery)
            }

            is UiAction.SetExpanded -> updateUiState {
                UiState.isSearchBarExpanded.set(this, uiAction.expanded)
            }

            is UiAction.OnPermissionChange -> updateUiState {
                UiState.isCallLogPermissionGranted.set(this, uiAction.accepted)
            }

            UiAction.OnSettingsIconClicked -> emitSideEffect(SideEffect.NavigateToSetting)
        }
    }
}
