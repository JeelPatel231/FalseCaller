package tel.jeelpa.falsecaller.ui.screens.settings

import android.content.Context
import android.preference.Preference
import android.preference.SwitchPreference
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.Flow
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

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
        val sharedPrefs = ctx.applicationContext.getSharedPreferences("default", Context.MODE_PRIVATE)
        ProvidePreferenceLocals(
            flow = sharedPrefs.getPreferenceFlow()
        ) {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize()
            ) {
                textFieldPreference(
                    key = "TRUECALLER_TOKEN",
                    title = { Text("Truecaller Token") },
                    defaultValue = "",
                    textToValue = { it },
                )
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
