package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 언어 선택 다이얼로그 - 🆕 배경색 이슈 수정
 */
@Composable
fun LanguageDialog(
    languages: List<Language>,
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        // 🆕 배경색 제거 - 투명하게 처리
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f) // 🆕 팝업 가로 길이 90%로 더 늘림 (80% -> 90%)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp), // 더 둥글게
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp) // 🆕 그림자 더 강화
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
            .background(
                if (isSelected) Color(0xFFE0E0E0).copy(alpha = 0.3f) 
                else Color.Transparent
            )
            .padding(vertical = 16.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🆕 국기 이미지 - 크기 통일 및 고정 너비 적용
        Box(
            modifier = Modifier.width(56.dp), // 고정 너비로 국기 영역 확보
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = language.flagResId),
                contentDescription = language.displayName,
                modifier = Modifier.size(36.dp) // 🆕 모든 국기 크기 36dp로 통일
            )
        }
        
        // 🆕 언어 이름 - 가운데 정렬
        Box(
            modifier = Modifier.weight(1f), // 남은 공간 모두 차지
            contentAlignment = Alignment.Center // 가운데 정렬
        ) {
            Text(
                text = language.displayName,
                style = AppTextStyles.pretendardXLargeSemiBold.copy(fontSize = 20.sp),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Preview(
    name = "LanguageDialog - English Selected - Fixed Background",
    group = "LanguageDialog",
    showBackground = true,
    widthDp = 800,
    heightDp = 360,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun LanguageDialogPreviewEnglishFixed() {
    KidsStoryTheme {
        // 🆕 배경 없이 다이얼로그만 표시
        LanguageDialog(
            languages = LanguageConstants.SUPPORTED_LANGUAGES,
            selectedLanguage = LanguageConstants.ENGLISH,
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(
    name = "LanguageDialog - Korean Selected - Fixed Background",
    group = "LanguageDialog",
    showBackground = true,
    widthDp = 800,
    heightDp = 360,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun LanguageDialogPreviewKoreanFixed() {
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
    name = "LanguageDialog - Tetum Selected - Fixed Background",
    group = "LanguageDialog",
    showBackground = true,
    widthDp = 800,
    heightDp = 360,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun LanguageDialogPreviewTetumFixed() {
    KidsStoryTheme {
        LanguageDialog(
            languages = LanguageConstants.SUPPORTED_LANGUAGES,
            selectedLanguage = LanguageConstants.TETUM,
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}
