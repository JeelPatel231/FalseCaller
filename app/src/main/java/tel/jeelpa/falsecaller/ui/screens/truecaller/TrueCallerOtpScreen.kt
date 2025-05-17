package tel.jeelpa.falsecaller.ui.screens.truecaller

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.compose.viewmodel.koinViewModel
import tel.jeelpa.falsecaller.lifecycle.RepeatOnLifecycle
import tel.jeelpa.falsecaller.ui.screens.truecaller.TrueCallerOtpContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.truecaller.TrueCallerOtpContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.truecaller.TrueCallerOtpContract.UiState

@Destination<RootGraph>
@Composable
fun TrueCallerOtpScreen(
    navigator: DestinationsNavigator,
) {
    val viewModel: TrueCallerOtpScreenViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    StatelessTrueCallerOtpScreen(
        navigator = navigator,
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        sideEffect = viewModel.sideEffect,
        onAction = viewModel::onAction,
    )
}

@Composable
fun StatelessTrueCallerOtpScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    uiState: UiState,
    sideEffect: Flow<SideEffect>,
    onAction: (UiAction) -> Unit,
) {
    val ctx = LocalContext.current

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row {
                TextField(enabled = uiState.sendOtpResponse == null,
                    modifier = Modifier.weight(1F),
                    value = uiState.number,
                    label = { Text("Phone Number") },
                    onValueChange = {
                        onAction(UiAction.UpdateNumber(it))
                    })

                Button(enabled = uiState.sendOtpResponse == null, onClick = {
                    onAction(UiAction.SendOtp(uiState.number))
                }) {
                    Text("Send OTP")
                }
            }

            Row {
                TextField(enabled = uiState.sendOtpResponse != null,
                    modifier = Modifier.weight(1F),
                    value = uiState.otp,
                    label = { Text("OTP") },
                    onValueChange = {
                        onAction(UiAction.UpdateOtp(it))
                    })

                Button(enabled = uiState.sendOtpResponse != null, onClick = {
                    onAction(
                        UiAction.VerifyOtp(
                            uiState.number, uiState.otp, uiState.sendOtpResponse!!
                        )
                    )
                }) {
                    Text("Verify OTP")
                }
            }
        }
    }

    RepeatOnLifecycle {
        sideEffect.collect {
            when (it) {
                is SideEffect.Toast -> Toast.makeText(ctx, it.text, Toast.LENGTH_SHORT).show()
                SideEffect.NavigateBack -> navigator.popBackStack()
            }
        }
    }
}


@Preview
@Composable
private fun _preview() {
    Surface {
        StatelessTrueCallerOtpScreen(
            uiState = UiState("", ""),
            sideEffect = emptyFlow(),
            onAction = {},
            navigator = EmptyDestinationsNavigator
        )
    }
}
