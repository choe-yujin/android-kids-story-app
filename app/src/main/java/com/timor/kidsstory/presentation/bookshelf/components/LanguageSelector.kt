package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 언어 선택 컴포넌트
 *
 * @param currentLanguage 현재 선택된 언어
 * @param onClick 언어 선택 버튼 클릭 이벤트
 */
@Composable
fun LanguageSelector(
    currentLanguage: Language,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 국기 이미지
            Image(
                painter = painterResource(id = currentLanguage.flagResId),
                contentDescription = "Flag of ${currentLanguage.displayName}",
                modifier = Modifier.size(24.dp)
            )

            // 드롭다운 아이콘
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select Language",
                modifier = Modifier.size(20.dp).padding(start = 4.dp)
            )
        }
    }
}

@Preview(
    name = "LanguageSelector - English",
    group = "LanguageSelector",
    showBackground = true
)
@Composable
fun LanguageSelectorPreviewEnglish() {
    KidsStoryTheme {
        LanguageSelector(
            currentLanguage = LanguageConstants.ENGLISH,
            onClick = {}
        )
    }
}

@Preview(
    name = "LanguageSelector - Korean",
    group = "LanguageSelector",
    showBackground = true
)
@Composable
fun LanguageSelectorPreviewKorean() {
    KidsStoryTheme {
        LanguageSelector(
            currentLanguage = LanguageConstants.KOREAN,
            onClick = {}
        )
    }
}

@Preview(
    name = "LanguageSelector - Tetum",
    group = "LanguageSelector",
    showBackground = true
)
@Composable
fun LanguageSelectorPreviewTetum() {
    KidsStoryTheme {
        LanguageSelector(
            currentLanguage = LanguageConstants.TETUM,
            onClick = {}
        )
    }
}