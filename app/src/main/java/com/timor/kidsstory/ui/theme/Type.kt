package com.timor.kidsstory.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = AppTextStyles.titleTextBold,
    displayMedium = AppTextStyles.titleTextRegular,
    displaySmall = AppTextStyles.headerTextBold,
    headlineLarge = AppTextStyles.headerTextRegular,
    headlineMedium = AppTextStyles.largeTextBold,
    headlineSmall = AppTextStyles.largeTextRegular,
    titleLarge = AppTextStyles.mediumTextBold,
    titleMedium = AppTextStyles.mediumTextRegular,
    bodyLarge = AppTextStyles.normalTextBold,
    bodyMedium = AppTextStyles.normalTextRegular,
    labelLarge = AppTextStyles.smallTextBold,
    labelMedium = AppTextStyles.smallTextRegular,
    labelSmall = AppTextStyles.smallerTextBold
)

val pretendardFamily = FontFamily(
    Font(R.font.pretendard_variable) // Variable Font 사용
)

object AppTextStyles {
    // Title Text Styles
    val titleTextBold = TextStyle(
        fontSize = 50.sp,
        lineHeight = 75.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = pretendardFamily
    )

    val titleTextRegular = TextStyle(
        fontSize = 50.sp,
        lineHeight = 75.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = pretendardFamily
    )

    // Header Text Styles
    val headerTextBold = TextStyle(
        fontSize = 30.sp,
        lineHeight = 45.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = pretendardFamily
    )

    val headerTextRegular = TextStyle(
        fontSize = 30.sp,
        lineHeight = 45.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = pretendardFamily
    )

    // Large Text Styles
    val largeTextBold = TextStyle(
        fontSize = 20.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = pretendardFamily
    )

    val largeTextRegular = TextStyle(
        fontSize = 20.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = pretendardFamily
    )

    // Medium Text Styles
    val mediumTextBold = TextStyle(
        fontSize = 18.sp,
        lineHeight = 27.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = pretendardFamily
    )

    val mediumTextRegular = TextStyle(
        fontSize = 18.sp,
        lineHeight = 27.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = pretendardFamily
    )

    // Normal Text Styles
    val normalTextBold = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = pretendardFamily
    )

    val normalTextRegular = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = pretendardFamily
    )

    // Small Text Styles
    val smallTextBold = TextStyle(
        fontSize = 14.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = pretendardFamily
    )

    val smallTextRegular = TextStyle(
        fontSize = 14.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = pretendardFamily
    )

    // Smaller Text Styles
    val smallerTextBold = TextStyle(
        fontSize = 11.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = pretendardFamily
    )

    val smallerTextRegular = TextStyle(
        fontSize = 11.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = pretendardFamily
    )
}