package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

@Composable
fun FullImagePageLayout(
    pageState: PageUiState,
    currentLanguage: String = "ko",
    onBackToBookshelf: () -> Unit = {},
    onTextToSpeech: (List<String>) -> Unit = {},
    isTetumTtsReady: Boolean,
    isDownloadingModel: Boolean,
    downloadProgress: Float,
    showTtsDownloadDialog: Boolean,
    ttsErrorMessage: String?,
    onDownloadTtsModel: () -> Unit,
    onDismissTtsDialog: () -> Unit,
    onDismissTtsError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pageState.imageUrl)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
            contentScale = ContentScale.FillWidth
        )

        if (pageState.texts.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                val textStyle = AppTextStyles.pretendardMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pageState.texts.forEach { text ->
                        if (text.isNotBlank()) {
                            Text(
                                text = text,
                                style = textStyle,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        if (isTablet) {
            val buttonSize = (32 * ResponsiveTextUtils.getScreenScaleFactor()).dp
            val iconSize = (21 * ResponsiveTextUtils.getScreenScaleFactor()).dp
            val padding = (16 * ResponsiveTextUtils.getScreenScaleFactor()).dp

            Box(
                modifier = Modifier
                    .padding(padding)
                    .align(Alignment.TopStart)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
            ) {
                IconButton(
                    onClick = onBackToBookshelf,
                    modifier = Modifier.size(buttonSize)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to bookshelf",
                        tint = Color.White,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }

        if (pageState.texts.isNotEmpty() && pageState.texts.any { it.isNotBlank() }) {
            val buttonSize = (32 * ResponsiveTextUtils.getScreenScaleFactor()).dp
            val iconSize = (21 * ResponsiveTextUtils.getScreenScaleFactor()).dp

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                IconButton(
                    onClick = { onTextToSpeech(pageState.texts) },
                    modifier = Modifier.size(buttonSize)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = "Text to speech",
                        tint = Color.White,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }

        if (showTtsDownloadDialog) {
            AlertDialog(
                onDismissRequest = {
                    if (!isDownloadingModel) {
                        onDismissTtsDialog()
                    }
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = !isDownloadingModel,
                    dismissOnClickOutside = !isDownloadingModel
                ),
                title = {
                    LocalizedText(
                        resId = R.string.tts_download_title,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (ttsErrorMessage != null) {
                            Text(
                                text = ttsErrorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else if (isDownloadingModel) {
                            LocalizedText(
                                resId = R.string.tts_downloading,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = downloadProgress,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${(downloadProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            LocalizedText(
                                resId = R.string.tts_download_message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                confirmButton = {
                    if (ttsErrorMessage != null) {
                        Button(onClick = { onDismissTtsError() }) {
                            LocalizedText(
                                resId = R.string.attendance_popup_button_ok,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else if (!isDownloadingModel) {
                        Button(onClick = {
                            onDownloadTtsModel()
                        }) {
                            LocalizedText(
                                resId = R.string.tts_download_button,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                dismissButton = {
                    if (ttsErrorMessage == null && !isDownloadingModel) {
                        Button(onClick = { onDismissTtsDialog() }) {
                            LocalizedText(
                                resId = R.string.tts_download_later,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 600, widthDp = 800)
@Composable
private fun FullImagePageLayoutPreview() {
    KidsStoryTheme {
        FullImagePageLayout(
            pageState = PageUiState(
                imageUrl = "",
                texts = listOf("이것은 전체 이미지 레이아웃의 예시입니다.", "텍스트는 이미지 하단에 오버레이로 표시됩니다."),
                pageNumber = 1,
                totalPages = 10,
                pageType = "FULL_IMAGE",
                currentLanguageCode = "ko"
            ),
            currentLanguage = "ko",
            onBackToBookshelf = {},
            onTextToSpeech = {},
            isTetumTtsReady = false,
            isDownloadingModel = false,
            downloadProgress = 0f,
            showTtsDownloadDialog = true,
            ttsErrorMessage = null,
            onDownloadTtsModel = {},
            onDismissTtsDialog = {},
            onDismissTtsError = {}
        )
    }
}