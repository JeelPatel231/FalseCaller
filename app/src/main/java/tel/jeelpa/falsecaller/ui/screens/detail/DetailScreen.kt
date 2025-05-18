package tel.jeelpa.falsecaller.ui.screens.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import arrow.core.None
import arrow.core.left
import arrow.core.right
import arrow.core.some
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import tel.jeelpa.falsecaller.lifecycle.RepeatOnLifecycle
import tel.jeelpa.falsecaller.models.CallLogEntry
import tel.jeelpa.falsecaller.models.CallerInfo
import tel.jeelpa.falsecaller.models.PhoneNumber
import tel.jeelpa.falsecaller.models.TriState
import tel.jeelpa.falsecaller.ui.components.CallerInfoDetails
import tel.jeelpa.falsecaller.ui.components.EmoticonError
import tel.jeelpa.falsecaller.ui.screens.detail.DetailContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.detail.DetailContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.detail.DetailContract.UiState

@Destination<RootGraph>
@Composable
fun DetailScreen(
    navigator: DestinationsNavigator,
    callLogEntry: CallLogEntry,
) {
    val viewModel: DetailScreenViewModel = koinViewModel { parametersOf(callLogEntry) }
    val uiState by viewModel.uiState.collectAsState()
    val callerInfo by viewModel.detailsFromTruecaller.collectAsState()
    StatelessDetailScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        sideEffect = viewModel.sideEffect,
        onAction = viewModel::onAction,
        callerDetails = callerInfo,
    )
}

@Composable
fun StatelessDetailScreen(
    modifier: Modifier = Modifier,
    uiState: UiState,
    sideEffect: Flow<SideEffect>,
    onAction: (UiAction) -> Unit,
    callerDetails: TriState<CallerInfo>,
) {
    val ctx = LocalContext.current

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            callerDetails
                .onNone {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .width(64.dp)
                                .height(64.dp)
                                .align(Alignment.Center),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
                .onSome { result ->
                    result
                        .onLeft {
                            EmoticonError(
                                modifier = Modifier.fillMaxSize(),
                                throwable = it
                            )
                        }
                        .onRight {
                            CallerInfoDetails(
                                modifier = Modifier.fillMaxSize(),
                                callerInfo = it
                            )
                        }

                }
        }
    }

    RepeatOnLifecycle {
        sideEffect.collect {
            when (it) {
                is SideEffect.Toast -> Toast.makeText(ctx, it.text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Preview
@Composable
private fun _detailsLoading() {
    Surface {
        StatelessDetailScreen(
            uiState = UiState(
                CallLogEntry(
                    name = "",
                    number = PhoneNumber(""),
                    avatarUri = null
                )
            ),
            sideEffect = emptyFlow(),
            onAction = {},
            callerDetails = None,
        )
    }
}

@Preview
@Composable
private fun _detailsSuccess() {
    Surface {
        StatelessDetailScreen(
            uiState = UiState(
                CallLogEntry(
                    name = "",
                    number = PhoneNumber(""),
                    avatarUri = null
                )
            ),
            sideEffect = emptyFlow(),
            onAction = {},
            callerDetails = CallerInfo(
                name = "John Doe",
                score = 0.0,
                spamScore = 0.0,
            ).right().some(),
        )
    }
}

@Preview
@Composable
private fun _detailsError() {
    Surface {
        StatelessDetailScreen(
            uiState = UiState(
                CallLogEntry(
                    name = "",
                    number = PhoneNumber(""),
                    avatarUri = null
                )
            ),
            sideEffect = emptyFlow(),
            onAction = {},
            callerDetails = IllegalStateException("Failed to fetch info.").left().some(),
        )
    }
}
