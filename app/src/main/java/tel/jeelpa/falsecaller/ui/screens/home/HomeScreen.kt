package tel.jeelpa.falsecaller.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import arrow.core.None
import arrow.core.Option
import arrow.core.some
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.compose.viewmodel.koinViewModel
import tel.jeelpa.falsecaller.lifecycle.RepeatOnLifecycle
import tel.jeelpa.falsecaller.models.CallLogEntry
import tel.jeelpa.falsecaller.ui.components.CallLogListEntry
import tel.jeelpa.falsecaller.ui.components.EmoticonError
import tel.jeelpa.falsecaller.ui.screens.home.HomeContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.home.HomeContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.home.HomeContract.UiState

@Destination<RootGraph>(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
) {
    val viewModel: HomeScreenViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val intermediateCallLogEntry by viewModel.intermediateCallLogEntry.collectAsState()

    StatelessHomeScreen(
        navigator = navigator,
        uiState = uiState,
        sideEffect = viewModel.sideEffect,
        onAction = viewModel::onAction,
        callLogs = viewModel.callLogs,
        searchedLogs = viewModel.searchResults,
        intermediateCallLogEntry = intermediateCallLogEntry,
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StatelessHomeScreen(
    navigator: DestinationsNavigator,
    uiState: UiState,
    sideEffect: Flow<SideEffect>,
    onAction: (UiAction) -> Unit,
    callLogs: Flow<PagingData<CallLogEntry>>,
    searchedLogs: Flow<PagingData<CallLogEntry>>,
    intermediateCallLogEntry: Option<CallLogEntry>,
) {
    val ctx = LocalContext.current

    val callLogPermissionState = rememberPermissionState(android.Manifest.permission.READ_CALL_LOG)

    LaunchedEffect(callLogPermissionState.status) {
        onAction(UiAction.OnPermissionChange(callLogPermissionState.status.isGranted))
    }

    RepeatOnLifecycle {
        sideEffect.collect {
            when (it) {
                is SideEffect.Toast -> Toast.makeText(ctx, it.text, Toast.LENGTH_SHORT).show()
                is SideEffect.NavigateToDetails -> navigator.navigate(DetailScreenDestination(it.data))
                SideEffect.NavigateToSetting -> navigator.navigate(SettingsScreenDestination)
            }
        }
    }

    val callLogEntries = callLogs.collectAsLazyPagingItems()
    val searchFilteredItems = searchedLogs.collectAsLazyPagingItems()

    Scaffold(topBar = {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = 0f },
                inputField = {
                    SearchBarDefaults.InputField(modifier = Modifier.padding(horizontal = 8.dp),
                        query = uiState.searchQuery,
                        onQueryChange = { onAction(UiAction.OnQueryChange(it)) },
                        onSearch = { },
                        expanded = uiState.isSearchBarExpanded,
                        onExpandedChange = { onAction(UiAction.SetExpanded(it)) },
                        placeholder = { Text("Search") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search"
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { onAction(UiAction.OnSettingsIconClicked) },
                                content = {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = "Settings"
                                    )
                                },
                            )
                        })
                },
                expanded = uiState.isSearchBarExpanded,
                onExpandedChange = { onAction(UiAction.SetExpanded(it)) },
            ) {
                LazyColumn {
                    if (uiState.searchQuery.isNotBlank()) {
                        // live edit entry for navigating to info window.
                        intermediateCallLogEntry.onSome {
                            item {
                                CallLogListEntry(modifier = Modifier
                                    .clickable {
                                        onAction(
                                            UiAction.OnCallLogItemClicked(it)
                                        )
                                        onAction(UiAction.SetExpanded(false))
                                    }
                                    .fillMaxWidth(), data = it)
                            }
                        }
                    }
                    items(searchFilteredItems.itemCount) { idx ->
                        val item = searchFilteredItems[idx]
                            ?: error("Paged Item index access was null (pointed to a placeholder)")
                        CallLogListEntry(modifier = Modifier
                            .clickable {
                                onAction(UiAction.OnCallLogItemClicked(item))
                                onAction(UiAction.SetExpanded(false))
                            }
                            .fillMaxWidth(), data = item)
                    }
                }
            }
        }
    }) { innerPadding ->
        if (!uiState.isCallLogPermissionGranted) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                EmoticonError(
                    modifier = Modifier,
                    errorMessage = "Grant Call Logs permission to show call history."
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    modifier = Modifier.height(56.dp),
                    onClick = { callLogPermissionState.launchPermissionRequest() }
                ) {
                    Text("Grant Permission")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
            ) {
                items(callLogEntries.itemCount) { idx ->
                    val item =
                        callLogEntries[idx]
                            ?: error("Index value was null (pointed to a placeholder)")

                    CallLogListEntry(
                        modifier = Modifier
                            .clickable {
                                onAction(UiAction.OnCallLogItemClicked(item))
                            }
                            .fillMaxWidth(),
                        data = item,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun _ExpandedSearchBar() {
    Surface {
        StatelessHomeScreen(
            navigator = EmptyDestinationsNavigator,
            uiState = UiState.default().copy(
                searchQuery = "Test",
                isSearchBarExpanded = true,
            ),
            sideEffect = emptyFlow(),
            onAction = {},
            searchedLogs = emptyFlow(),
            callLogs = emptyFlow(),
            intermediateCallLogEntry = CallLogEntry("Name", PhoneNumber(), null).some(),
        )
    }
}

@Preview
@Composable
private fun _HomeScreenDefault() {
    val callLog = (0..10).map {
        CallLogEntry(
            name = "Person $it", number = PhoneNumber(), avatarUri = ""
        )
    }
    Surface {
        StatelessHomeScreen(
            navigator = EmptyDestinationsNavigator,
            uiState = UiState.default().copy(
                isCallLogPermissionGranted = false
            ),
            sideEffect = emptyFlow(),
            onAction = {},
            searchedLogs = emptyFlow(),
            callLogs = emptyFlow(),
            intermediateCallLogEntry = None,
        )
    }
}
