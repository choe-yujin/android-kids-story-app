package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 언어 선택 다이얼로그
 */
@Composable
fun LanguageDialog(
    languages: List<Language>,
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
//                Text(
//                    text = "Select Language",
//                    style = AppTextStyles.pretendardXLargeSemiBold,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )

                languages.forEach { language ->
                    LanguageItem(
                        language = language,
                        isSelected = language.code == selectedLanguage.code,
                        onClick = {
                            onLanguageSelected(language)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageItem(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) Color(0xFFE0E0E0) else Color.Transparent)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 국기 이미지
        Image(
            painter = painterResource(id = language.flagResId),
            contentDescription = language.displayName,
            modifier = Modifier.size(24.dp)
        )

        // 언어 이름
        Text(
            text = language.displayName,
            style = AppTextStyles.pretendardXLargeSemiBold,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(
    name = "LanguageDialog - English Selected",
    group = "LanguageDialog",
    showBackground = true,
    widthDp = 800,
    heightDp = 360,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun LanguageDialogPreviewEnglish() {
    KidsStoryTheme {
        LanguageDialog(
            languages = LanguageConstants.SUPPORTED_LANGUAGES,
            selectedLanguage = LanguageConstants.ENGLISH,
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(
    name = "LanguageDialog - Korean Selected",
    group = "LanguageDialog",
    showBackground = true,
    widthDp = 800,
    heightDp = 360,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun LanguageDialogPreviewKorean() {
    KidsStoryTheme {
        LanguageDialog(
            languages = LanguageConstants.SUPPORTED_LANGUAGES,
            selectedLanguage = LanguageConstants.KOREAN,
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(
    name = "LanguageDialog - Tetum Selected",
    group = "LanguageDialog",
    showBackground = true,
    widthDp = 800,
    heightDp = 360,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun LanguageDialogPreviewTetum() {
    KidsStoryTheme {
        LanguageDialog(
            languages = LanguageConstants.SUPPORTED_LANGUAGES,
            selectedLanguage = LanguageConstants.TETUM,
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}