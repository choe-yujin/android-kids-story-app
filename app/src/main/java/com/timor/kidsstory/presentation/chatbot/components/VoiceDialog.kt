package com.timor.kidsstory.presentation.chatbot.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.KidsStoryTheme

@Composable
fun VoiceDialog(
    onDismissRequest: () -> Unit = { },
    cancelVoiceSearch: () -> Unit = { },
    onCloseClick: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        )
        {
            Text(
                text = "Voice recognition is in progress..."
            )

            Spacer(modifier = Modifier.height(22.dp))

            Icon(
                modifier = Modifier.size(72.dp),
                painter = painterResource(R.drawable.voice_icon), contentDescription = null,
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = cancelVoiceSearch,
                colors = ButtonColors(
                    containerColor = AppColors.blue600,
                    contentColor = Color.Black, disabledContentColor = Color.Gray, disabledContainerColor = Color.Gray
                )
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                    text = "Stop voice"
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 59.dp, bottom = 18.dp),
                onClick = onCloseClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null
                )
            }
        }

    }
}


@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
private fun VoiceDialogPreview() {
    KidsStoryTheme {
        VoiceDialog()
    }
}