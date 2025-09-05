package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.Intent
import android.net.Uri
import com.timor.kidsstory.domain.model.AppVersionInfo
import com.timor.kidsstory.ui.theme.*

@Composable
fun AppUpdateDialog(
    versionInfo: AppVersionInfo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = if (versionInfo.isUpdateRequired) { {} } else onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = !versionInfo.isUpdateRequired,
            dismissOnClickOutside = !versionInfo.isUpdateRequired
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AppColors.neutralWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 아이콘 또는 이미지 영역
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.primary200),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉",
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 제목
                Text(
                    text = if (versionInfo.isUpdateRequired) "필수 업데이트" else "업데이트 알림",
                    style = Typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.neutral900
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 버전 정보
                Text(
                    text = "새 버전: ${versionInfo.latestVersionName}",
                    style = Typography.bodyLarge.copy(
                        color = AppColors.primary700,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 업데이트 메시지
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = versionInfo.updateMessage,
                            style = Typography.bodyMedium.copy(
                                color = AppColors.neutral500,
                                lineHeight = 24.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        // 릴리즈 노트가 있으면 표시
                        versionInfo.releaseNotes?.let { notes ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "변경사항:",
                                style = Typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.neutral900
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notes,
                                style = Typography.bodySmall.copy(
                                    color = AppColors.neutral500,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 버튼 섹션
                if (versionInfo.isUpdateRequired) {
                    // 필수 업데이트 - 업데이트 버튼만
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo.downloadUrl))
                            context.startActivity(intent)
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
                            text = "지금 업데이트",
                            style = Typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                } else {
                    // 선택적 업데이트 - 나중에 + 업데이트 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AppColors.neutral500
                            )
                        ) {
                            Text(
                                text = "나중에",
                                style = Typography.bodyLarge
                            )
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo.downloadUrl))
                                context.startActivity(intent)
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
                                text = "업데이트",
                                style = Typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}