package com.timor.kidsstory.ui.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.util.LanguageManager
import java.util.Locale

/**
 * 폰트 적용 정책
 */
enum class FontPolicy {
    /** 기본 정책: 한국어(CookieRun), 영어/테툼어(Gummy) */
    DEFAULT,
    /** 모든 언어 Pretendard */
    PRETENDA_ALL
}

/**
 * 다국어 처리와 폰트 정책을 모두 담당하는 통합 텍스트 컴포넌트
 *
 * - 태블릿의 Configuration 캐싱 이슈를 해결한 강제 언어 로직 내장
 * - `FontPolicy`를 통해 앱의 글꼴 정책을 중앙에서 관리
 *
 * @param resId 리소스 문자열 ID
 * @param modifier Modifier
 * @param style 기본 텍스트 스타일
 * @param color 텍스트 색상
 * @param textAlign 텍스트 정렬
 * @param maxLines 최대 줄 수
 * @param overflow 오버플로우 처리
 * @param fontSize 폰트 크기
 * @param fontPolicy 폰트 적용 정책 (기본값: DEFAULT)
 * @param formatArgs 포맷 인자 (배열 형태)
 */
@Composable
fun LocalizedText(
    @StringRes resId: Int,
    modifier: Modifier = Modifier,
    style: TextStyle,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontPolicy: FontPolicy = FontPolicy.DEFAULT,
    formatArgs: Array<out Any>? = null
) {
    val context = LocalContext.current

    // 폰트 정의
    val cookieRunFamily = FontFamily(Font(R.font.cookierun_regular))
    val gummyFamily = FontFamily(Font(R.font.gummy_variable))
    val pretendardFamily = FontFamily(Font(R.font.pretendard_variable))

    // 🆕 LanguageManager의 상태를 관찰하도록 변경
    val currentLanguageCode = LanguageManager.getCurrentLanguageCode()

    // 언어 코드와 포맷 인자에 따라 텍스트를 기억
    val text = remember(currentLanguageCode, resId, formatArgs) {
        val locale = Locale(currentLanguageCode)
        val config = Configuration(context.resources.configuration).apply { setLocale(locale) }
        val localizedContext = context.createConfigurationContext(config)
        if (formatArgs != null) {
            localizedContext.getString(resId, *formatArgs)
        } else {
            localizedContext.getString(resId)
        }
    }

    // 폰트 정책에 따라 글꼴 결정
    val finalFontFamily = remember(fontPolicy, currentLanguageCode) {
        when (fontPolicy) {
            FontPolicy.PRETENDA_ALL -> pretendardFamily
            FontPolicy.DEFAULT -> {
                when (currentLanguageCode) {
                    "ko" -> cookieRunFamily
                    "en", "tet" -> gummyFamily
                    else -> gummyFamily // 기본값으로 Gummy 사용
                }
            }
        }
    }

    // 최종 스타일 적용
    val finalStyle = style.copy(fontFamily = finalFontFamily)

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

/**
 * 복수형 처리를 지원하는 다국어 텍스트 컴포넌트
 *
 * @param pluralsResId Plurals 리소스 ID
 * @param quantity 수량 (복수형 결정에 사용)
 * @param modifier Modifier
 * @param style 기본 텍스트 스타일
 * @param color 텍스트 색상
 * @param textAlign 텍스트 정렬
 * @param maxLines 최대 줄 수
 * @param overflow 오버플로우 처리
 * @param fontSize 폰트 크기
 * @param fontPolicy 폰트 적용 정책 (기본값: DEFAULT)
 * @param formatArgs 포맷 인자 (배열 형태)
 */
@Composable
fun LocalizedPluralText(
    @androidx.annotation.PluralsRes pluralsResId: Int,
    quantity: Int,
    modifier: Modifier = Modifier,
    style: TextStyle,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontPolicy: FontPolicy = FontPolicy.DEFAULT,
    formatArgs: Array<out Any>? = null
) {
    val context = LocalContext.current

    // 폰트 정의
    val cookieRunFamily = FontFamily(Font(R.font.cookierun_regular))
    val gummyFamily = FontFamily(Font(R.font.gummy_variable))
    val pretendardFamily = FontFamily(Font(R.font.pretendard_variable))

    // 🆕 LanguageManager의 상태를 관찰하도록 변경
    val currentLanguageCode = LanguageManager.getCurrentLanguageCode()

    // 언어 코드와 포맷 인자에 따라 텍스트를 기억
    val text = remember(currentLanguageCode, pluralsResId, quantity, formatArgs) {
        val locale = Locale(currentLanguageCode)
        val config = Configuration(context.resources.configuration).apply { setLocale(locale) }
        val localizedContext = context.createConfigurationContext(config)
        if (formatArgs != null) {
            localizedContext.resources.getQuantityString(pluralsResId, quantity, *formatArgs)
        } else {
            localizedContext.resources.getQuantityString(pluralsResId, quantity, quantity)
        }
    }

    // 폰트 정책에 따라 글꼴 결정
    val finalFontFamily = remember(fontPolicy, currentLanguageCode) {
        when (fontPolicy) {
            FontPolicy.PRETENDA_ALL -> pretendardFamily
            FontPolicy.DEFAULT -> {
                when (currentLanguageCode) {
                    "ko" -> cookieRunFamily
                    "en", "tet" -> gummyFamily
                    else -> gummyFamily // 기본값으로 Gummy 사용
                }
            }
        }
    }

    // 최종 스타일 적용
    val finalStyle = style.copy(fontFamily = finalFontFamily)

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
