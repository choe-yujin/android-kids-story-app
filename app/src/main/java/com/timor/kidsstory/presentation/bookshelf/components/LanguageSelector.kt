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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

/**
 * 언어 선택 컴포넌트
 *
 * @param currentLanguage 현재 선택된 언어
 * @param iconSize 아이콘 크기 (설정 아이콘과 동일하게)
 * @param onClick 언어 선택 버튼 클릭 이벤트
 */
@Composable
fun LanguageSelector(
    currentLanguage: Language,
    iconSize: Dp? = null,  // 옵션널 파라미터 추가
    onClick: () -> Unit,
) {
    // 반응형 크기 계산 (기본값 또는 전달받은 값 사용)
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    val selectorSize = iconSize ?: (36 * scaleFactor).dp
    
    Box(
        modifier = Modifier
            .size(selectorSize)
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
    name = "LanguageSelector - Phone",
    group = "LanguageSelector",
    showBackground = true,
    widthDp = 50,
    heightDp = 50
)
@Composable
fun LanguageSelectorPreviewPhone() {
    KidsStoryTheme {
        LanguageSelector(
            currentLanguage = LanguageConstants.ENGLISH,
            onClick = {}
        )
    }
}

@Preview(
    name = "LanguageSelector - Tablet",
    group = "LanguageSelector",
    showBackground = true,
    widthDp = 70,
    heightDp = 70,
    device = "spec:width=800dp,height=600dp"
)
@Composable
fun LanguageSelectorPreviewTablet() {
    KidsStoryTheme {
        LanguageSelector(
            currentLanguage = LanguageConstants.KOREAN,
            onClick = {}
        )
    }
}

@Preview(
    name = "LanguageSelector - Large Tablet",
    group = "LanguageSelector",
    showBackground = true,
    widthDp = 80,
    heightDp = 80,
    device = "spec:width=1024dp,height=768dp"
)
@Composable
fun LanguageSelectorPreviewLargeTablet() {
    KidsStoryTheme {
        LanguageSelector(
            currentLanguage = LanguageConstants.TETUM,
            onClick = {}
        )
    }
}
