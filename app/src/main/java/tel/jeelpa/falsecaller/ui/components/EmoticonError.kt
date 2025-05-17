package tel.jeelpa.falsecaller.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmoticonError(
    modifier: Modifier = Modifier,
    throwable: Throwable
) {
    EmoticonError(
        modifier = modifier,
        errorMessage = throwable.localizedMessage ?: throwable.message ?: "Unknown Error",
    )
}

val SAD_EMOTICONS = arrayOf(
    "( ; _ ;)",
    "( . _ .)",
    "( . - .)",
)

@Composable
fun EmoticonError(
    modifier: Modifier = Modifier,
    errorMessage: String
){
    val emoticon = SAD_EMOTICONS.random()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = emoticon,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier
            .height(24.dp))
        Text(
            modifier = Modifier.fillMaxWidth(0.7F),
            textAlign = TextAlign.Center,
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
    }
}