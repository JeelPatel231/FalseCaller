package tel.jeelpa.falsecaller

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import tel.jeelpa.falsecaller.services.OverlayService
import tel.jeelpa.falsecaller.ui.theme.FalseCallerTheme
import tel.jeelpa.falsecaller.ui.theme.SlideTransitions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FalseCallerTheme {
                Surface {
                    DestinationsNavHost(
                        defaultTransitions = SlideTransitions(),
                        navGraph = NavGraphs.root,
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FalseCallerTheme {
        Greeting("Android")
    }
}