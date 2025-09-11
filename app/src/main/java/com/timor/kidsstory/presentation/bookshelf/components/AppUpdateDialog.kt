package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.AppVersionInfo
import com.timor.kidsstory.domain.util.LanguageManager
import com.timor.kidsstory.ui.theme.*

@Composable
fun AppUpdateDialog(
    versionInfo: AppVersionInfo,
    onDismiss: () -> Unit,
    onPostpone: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    
    // 현재 언어 코드 로깅
    val currentLanguage = LanguageManager.getCurrentLanguageCode()
    Log.d("AppUpdateDialog", "Current language code: $currentLanguage")
    Log.d("AppUpdateDialog", "Update message: ${versionInfo.updateMessage}")
    Log.d("AppUpdateDialog", "Release notes: ${versionInfo.releaseNotes}")
    
    // 화면 크기에 따른 동적 크기 설정 - 가로 길게, 세로 짧게
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    
    // 요청대로 가로 길게, 세로 짧게
    val dialogWidthFraction = when {
        screenWidthDp >= 1000 -> 0.9f   // 대형 태블릿 - 90%
        screenWidthDp >= 800 -> 0.95f   // 중형 태블릿 - 95%
        screenWidthDp >= 600 -> 0.98f   // 소형 태블릿 - 98%
        else -> 0.99f                   // 휴대폰 - 99% (최대한 넓게)
    }
    
    val dialogHeightFraction = when {
        screenHeightDp >= 800 -> 0.5f   // 높은 화면 - 50%
        screenHeightDp >= 600 -> 0.6f   // 중간 화면 - 60%
        else -> 0.7f                    // 낮은 화면 - 70%
    }

    Dialog(
        onDismissRequest = if (versionInfo.isUpdateRequired) { {} } else onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = !versionInfo.isUpdateRequired,
            dismissOnClickOutside = !versionInfo.isUpdateRequired
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(dialogWidthFraction)
                .fillMaxHeight(dialogHeightFraction)
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.neutralWhite),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 상단: 제목만 (버전 정보 제거)
                Text(
                    text = if (versionInfo.isUpdateRequired) 
                        stringResource(R.string.update_dialog_title_required)
                        else stringResource(R.string.update_dialog_title_optional),
                    style = Typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.neutral900,
                        fontSize = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )

                // 중앙: 메시지 카드 - 더 긴 공간 할당
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // 남은 공간 최대 활용
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F9FA)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()) // 내부 스크롤
                    ) {
                        // 업데이트 메시지 - 더 큰 폰트, 더 많은 공간
                        Text(
                            text = versionInfo.updateMessage,
                            style = Typography.bodyLarge.copy(
                                color = AppColors.neutral700,
                                lineHeight = 24.sp,
                                fontSize = 16.sp // 크기 증가
                            ),
                            textAlign = TextAlign.Start
                        )

                        // 릴리즈 노트 - 더 많은 공간
                        versionInfo.releaseNotes?.let { notes ->
                            Spacer(modifier = Modifier.height(18.dp))
                            HorizontalDivider(
                                color = Color(0xFFE5E7EB), 
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            
                            // "변경사항" 제목
                            Text(
                                text = stringResource(R.string.update_dialog_release_notes),
                                style = Typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.neutral900,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = notes,
                                style = Typography.bodyMedium.copy(
                                    color = AppColors.neutral600,
                                    lineHeight = 22.sp,
                                    fontSize = 15.sp // 크기 증가
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 하단: 버튼 영역 - 한 줄에 2개
                if (versionInfo.isUpdateRequired) {
                    // 필수 업데이트 - 업데이트 버튼만 전체 너비
                    Button(
                        onClick = {
                            Log.d("AppUpdateDialog", "Update button clicked")
                            try {
                                val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("market://details?id=${context.packageName}")
                                    setPackage("com.android.vending")
                                }
                                context.startActivity(playStoreIntent)
                            } catch (e: Exception) {
                                Log.w("AppUpdateDialog", "PlayStore app not found, opening in browser", e)
                                val webIntent = Intent(Intent.ACTION_VIEW, 
                                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                                context.startActivity(webIntent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.primary700
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.update_dialog_button_update_now),
                            style = Typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )
                    }
                } else {
                    // 선택적 업데이트 - 한 줄에 2개 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 업데이트 버튼 (주요 액션)
                        Button(
                            onClick = {
                                Log.d("AppUpdateDialog", "Update button clicked")
                                try {
                                    val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("market://details?id=${context.packageName}")
                                        setPackage("com.android.vending")
                                    }
                                    context.startActivity(playStoreIntent)
                                } catch (e: Exception) {
                                    Log.w("AppUpdateDialog", "PlayStore app not found, opening in browser", e)
                                    val webIntent = Intent(Intent.ACTION_VIEW, 
                                        Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                                    context.startActivity(webIntent)
                                }
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.primary700
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.update_dialog_button_update),
                                style = Typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            )
                        }

                        // 나중에 버튼 (보조 액션)
                        OutlinedButton(
                            onClick = {
                                Log.d("AppUpdateDialog", "Later button clicked")
                                onPostpone()
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AppColors.neutral600
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.5.dp
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.update_dialog_button_later),
                                style = Typography.bodyMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}