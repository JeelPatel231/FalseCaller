package tel.jeelpa.falsecaller.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.TrueCallerOtpScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.getPreferenceFlow
import me.zhanghai.compose.preference.textFieldPreference
import org.koin.compose.viewmodel.koinViewModel
import tel.jeelpa.falsecaller.lifecycle.RepeatOnLifecycle
import tel.jeelpa.falsecaller.ui.screens.settings.SettingsContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.settings.SettingsContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.settings.SettingsContract.UiState

@Destination<RootGraph>
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
) {
    val viewModel: SettingsScreenViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    StatelessSettingsScreen(
        navigator = navigator,
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        sideEffect = viewModel.sideEffect,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun StatelessSettingsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    uiState: UiState,
    sideEffect: Flow<SideEffect>,
    onAction: (UiAction) -> Unit,
) {
    val ctx = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { onAction(UiAction.SetSystemAlertWindowPermission(Settings.canDrawOverlays(ctx))) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
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
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        val sharedPrefs =
            ctx.applicationContext.getSharedPreferences("default", Context.MODE_PRIVATE)
        ProvidePreferenceLocals(
            flow = sharedPrefs.getPreferenceFlow()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                textFieldPreference(
                    key = "TRUECALLER_TOKEN",
                    title = { Text("Truecaller Token") },
                    defaultValue = "",
                    textToValue = { it },
                )
                item {
                    ListItem(
                        modifier = Modifier.clickable {
                            onAction(UiAction.NavigateToOtpTokenScreen)
                        },
                        headlineContent = { Text("Login using OTP") }
                    )
                }
                item {
                    ListItem(
                        modifier = Modifier.clickable {
                            onAction(UiAction.NavigateToDrawOverOtherAppsPermissionWindow)
                        },
                        headlineContent = { Text("Floating Window Permission") },
                        trailingContent = {
                            Switch(
                                enabled = false,
                                checked = uiState.isSystemAlertWindowGranted,
                                onCheckedChange = { })
                        }
                    )
                }
            }
        }
    }

    RepeatOnLifecycle {
        sideEffect.collect {
            when (it) {
                is SideEffect.Toast -> Toast.makeText(ctx, it.text, Toast.LENGTH_SHORT).show()
                SideEffect.NavigateToOtpTokenScreen -> navigator.navigate(
                    TrueCallerOtpScreenDestination
                )

                SideEffect.NavigateToDrawOverOtherAppsPermissionWindow -> {
                    launcher.launch(
                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun _preview() {
    StatelessSettingsScreen(
        navigator = EmptyDestinationsNavigator,
        uiState = UiState.default(),
        sideEffect = emptyFlow(),
        onAction = {},
    )
}
