package com.timor.kidsstory.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.timor.kidsstory.R
import androidx.compose.ui.graphics.Color

/**
 * 리소스 문자열을 표시하는 컴포넌트
 * 한국어 리소스 문자열에 대해 쿠키런 폰트를 적용합니다.
 */
@Composable
fun LocalizedText(
    resId: Int,
    modifier: Modifier = Modifier,
    style: TextStyle,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    // 쿠키런 폰트 패밀리 정의
    val cookieRunFamily = FontFamily(
        Font(R.font.cookierun_regular)
    )

    // 리소스 문자열 가져오기
    val text = androidx.compose.ui.res.stringResource(resId)

    // 한국어 문자 포함 여부 확인
    val containsKorean = text.matches(Regex(".*[가-힣].*"))

    // 한국어 포함 시 쿠키런 폰트 적용, 아니면 원래 스타일 유지
    val finalStyle = if (containsKorean) {
        style.copy(fontFamily = cookieRunFamily)
    } else {
        style
    }

    Text(
        text = text,
        modifier = modifier,
        style = if (fontSize != TextUnit.Unspecified) finalStyle.copy(fontSize = fontSize) else finalStyle,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}
