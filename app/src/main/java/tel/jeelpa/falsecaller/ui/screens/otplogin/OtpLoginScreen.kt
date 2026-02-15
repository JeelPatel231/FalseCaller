package tel.jeelpa.falsecaller.ui.screens.otplogin

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.compose.viewmodel.koinViewModel
import tel.jeelpa.falsecaller.lifecycle.RepeatOnLifecycle
import tel.jeelpa.falsecaller.ui.screens.otplogin.OtpLoginContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.otplogin.OtpLoginContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.otplogin.OtpLoginContract.UiState
import androidx.core.content.edit
import tel.jeelpa.falsecaller.constants.Constants

@Destination<RootGraph>
@Composable
fun OtpLoginScreen(
    navigator: DestinationsNavigator,
) {
    val viewModel: OtpLoginViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    StatelessOtpLoginScreen(
        navigator = navigator,
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        sideEffect = viewModel.sideEffect,
        onAction = viewModel::onAction,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatelessOtpLoginScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    uiState: UiState,
    sideEffect: Flow<SideEffect>,
    onAction: (UiAction) -> Unit,
) {
    val ctx = LocalContext.current
    val sharedPrefs =
        ctx.applicationContext.getSharedPreferences("default", Context.MODE_PRIVATE)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    RepeatOnLifecycle {
        sideEffect.collect {
            when (it) {
                is SideEffect.Toast -> Toast.makeText(ctx, it.text, Toast.LENGTH_SHORT).show()
                is SideEffect.StoreTokenInSharedPrefs -> {
                    // save the token
                    sharedPrefs.edit(commit = true) {
                        putString(Constants.PreferenceKey.Token, it.token)
                    }
                    // show success
                    Toast.makeText(ctx, "Logged In!", Toast.LENGTH_SHORT).show()
                    // navigate back to the settings screen after showing a toast
                    navigator.popBackStack()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Login",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        val columnScrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(columnScrollState)
        ) {
            TextField(
                value = uiState.number,
                onValueChange = { newText ->
                    onAction(UiAction.PhoneNumberFieldChanged(newText))
                },
                label = { Text("Phone Number") },
                placeholder = { Text("+919912345678") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.step == OtpLoginContract.Step.PhoneNumber
            )

            Button(
                modifier = Modifier.align(Alignment.End),
                enabled = uiState.step == OtpLoginContract.Step.PhoneNumber,
                onClick = { onAction(UiAction.SendOtpToNumber(uiState.number)) }
            ) {
                Text("Send OTP")
            }


            TextField(
                value = uiState.otp,
                onValueChange = { newText ->
                    onAction(UiAction.OtpFieldChanged(newText))
                },
                label = { Text("OTP") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.step == OtpLoginContract.Step.Otp
            )

            Button(
                modifier = Modifier.align(Alignment.End),
                enabled = uiState.step == OtpLoginContract.Step.Otp,
                onClick = { onAction(UiAction.VerifyOtp(uiState.number, uiState.otp, uiState.sendOtpResponse!!)) }
            ) {
                Text("Verify OTP")
            }
        }
    }
}

@Preview
@Composable
private fun _preview() {
    StatelessOtpLoginScreen(
        navigator = EmptyDestinationsNavigator,
        uiState = UiState.default(),
        sideEffect = emptyFlow(),
        onAction = {},
    )
}
