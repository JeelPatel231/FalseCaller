package tel.jeelpa.falsecaller.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import arrow.core.toOption
import coil3.compose.AsyncImage
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import org.koin.compose.koinInject
import tel.jeelpa.falsecaller.models.CallLogEntry
import tel.jeelpa.falsecaller.utils.e164Format
import tel.jeelpa.falsecaller.utils.nullIfBlank

@Composable
fun AvatarFromName(modifier: Modifier = Modifier, name: String) {
    val split = name.split(" ").filter { it.isNotBlank() }

    val initialText = when (split.size) {
        0 -> "?"
        1 -> split.first().first().uppercase()
        else -> split.first().first().uppercase() + split.last().first().uppercase()
    }

    return Box(
        modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initialText,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
fun CallLogListEntry(
    modifier: Modifier = Modifier,
    data: CallLogEntry
) {
    // TODO: do something about this random inject
    val phoneNumberUtil: PhoneNumberUtil = koinInject()
    val leadingContent: @Composable (Modifier) -> Unit = { mod ->
        data.avatarUri.toOption().filter { it.isNotBlank() }.fold(
            ifEmpty = { AvatarFromName(modifier = mod, name = data.name) },
            ifSome = { AsyncImage(modifier = mod, model = it, contentDescription = null) }
        )
    }

    val numberString = phoneNumberUtil.e164Format(data.number)
    val headline = data.name.nullIfBlank() ?: numberString

    ListItem(
        modifier = modifier,
        headlineContent = { Text(headline) },
        supportingContent = { Text(numberString) },
        leadingContent = { leadingContent(Modifier
            .size(48.dp)
            .clip(CircleShape)
        ) },
    )
}


@Preview
@Composable
private fun _preview() {
    val a = CallLogEntry(
        name = "",
        number = PhoneNumber(),
        avatarUri = null
    )
    Column {
        CallLogListEntry(data = a)
        CallLogListEntry(data = a.copy(avatarUri = ""))
        CallLogListEntry(data = a.copy(name = "Human"))
        CallLogListEntry(data = a.copy(name = "Monkey D. Luffy"))
        CallLogListEntry(data = a.copy(name = "Nakuru Aitsuki"))
    }
}