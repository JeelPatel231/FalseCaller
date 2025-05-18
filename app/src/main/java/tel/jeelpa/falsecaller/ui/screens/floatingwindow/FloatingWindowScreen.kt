package tel.jeelpa.falsecaller.ui.screens.floatingwindow

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import tel.jeelpa.falsecaller.lifecycle.RepeatOnLifecycle
import tel.jeelpa.falsecaller.models.CallerInfo
import tel.jeelpa.falsecaller.models.PhoneNumber
import tel.jeelpa.falsecaller.models.TriState
import tel.jeelpa.falsecaller.ui.components.CallerInfoDetails
import tel.jeelpa.falsecaller.ui.components.EmoticonError
import tel.jeelpa.falsecaller.ui.screens.floatingwindow.FloatingWindowContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.floatingwindow.FloatingWindowContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.floatingwindow.FloatingWindowContract.UiState

@Composable
fun StatelessFloatingWindowScreen(
    modifier: Modifier = Modifier,
    uiState: UiState,
    sideEffect: Flow<SideEffect>,
    onAction: (UiAction) -> Unit,
    callerInfo: TriState<CallerInfo>,
) {
    val ctx = LocalContext.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        callerInfo
            .onNone {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .height(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            .onSome { result ->
                result
                    .onLeft { EmoticonError(throwable = it) }
                    .onRight { CallerInfoDetails(callerInfo = it) }
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

@Composable
fun FloatingWindowScreen(
    modifier: Modifier = Modifier,
    number: PhoneNumber,
) {
    val viewModel: FloatingWindowViewModel = koinViewModel { parametersOf(number) }
    val uiState by viewModel.uiState.collectAsState()
    val callerInfo by viewModel.detailsFromTruecaller.collectAsState()

    StatelessFloatingWindowScreen(
        modifier = modifier,
        uiState = uiState,
        sideEffect = viewModel.sideEffect,
        onAction = viewModel::onAction,
        callerInfo = callerInfo,
    )
}