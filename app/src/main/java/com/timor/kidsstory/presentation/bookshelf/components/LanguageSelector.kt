package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
            .size(36.dp)
            .clip(shape = CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = currentLanguage.flagResId),
            contentDescription = "Flag of ${currentLanguage.displayName}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
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