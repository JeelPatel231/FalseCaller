package tel.jeelpa.falsecaller.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tel.jeelpa.falsecaller.models.CallerInfo

@Composable
fun CallerInfoDetails(
    modifier: Modifier = Modifier,
    callerInfo: CallerInfo
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = callerInfo.name,
            style = MaterialTheme.typography.displayMedium,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Spam Score: ${callerInfo.spamScore}",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Score: ${callerInfo.score}",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview
@Composable
private fun _callerInfo() {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CallerInfoDetails(
                callerInfo = CallerInfo(
                    name = "John Doe",
                    score = 0.0,
                    spamScore = 0.0,
                )
            )
        }
    }
}