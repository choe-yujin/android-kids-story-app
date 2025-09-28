package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.StoryInfo
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles

/**
 * 동화 정보를 보여주는 다이얼로그 (미션 팝업 스타일)
 * - 줄거리, 사전 질문, 사후 질문 표시
 */
@Composable
fun StoryInfoDialog(
    storyInfo: StoryInfo,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.98f) // 🆕 가로 폭 더욱 확장 (0.95 -> 0.98)
                .fillMaxHeight(0.9f) // 🆕 높이도 더 확장 (0.8 -> 0.9)
                .padding(12.dp), // 🆕 패딩 약간 축소
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp) // 🆕 패딩 줍간
            ) {
                // 제목 (고정 - 1줄로 유지)
                LocalizedText(
                    resId = R.string.story_info_dialog_title,
                    style = AppTextStyles.gummyLgSemiboldItalic.copy(
                        fontSize = 24.sp, // 🆕 폰트 크기 약간 축소
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center,
                    maxLines = 1, // 🆕 제목은 반드시 1줄
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(20.dp))

                // 스크롤 가능한 콘텐츠 영역
                Column(
                    modifier = Modifier
                        .weight(1f) // 🆕 남은 공간을 모두 차지
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 4.dp) // 스크롤바 공간 확보
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 줄거리
                        if (!storyInfo.summary.isNullOrBlank()) {
                            Section(
                                titleResId = R.string.story_summary_label,
                                content = {
                                    Text(
                                        text = storyInfo.summary,
                                        style = AppTextStyles.gummyMedium.copy(fontSize = 14.sp),
                                        color = Color(0xFF424242),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                            )
                        }

                        // 사전 질문
                        if (storyInfo.preQuestions.isNotEmpty()) {
                            Section(
                                titleResId = R.string.pre_questions_label,
                                content = {
                                    storyInfo.preQuestions.forEachIndexed { index, question ->
                                        Text(
                                            text = "${index + 1}. $question",
                                            style = AppTextStyles.gummyMedium.copy(fontSize = 14.sp),
                                            color = Color(0xFF424242),
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }
                                }
                            )
                        }

                        // 사후 질문
                        if (storyInfo.postQuestions.isNotEmpty()) {
                            Section(
                                titleResId = R.string.post_questions_label,
                                content = {
                                    storyInfo.postQuestions.forEachIndexed { index, question ->
                                        Text(
                                            text = "${index + 1}. $question",
                                            style = AppTextStyles.gummyMedium.copy(fontSize = 14.sp),
                                            color = Color(0xFF424242),
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))

                // 닫기 버튼 (하단 고정)
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    LocalizedText(
                        resId = R.string.close_button,
                        style = AppTextStyles.gummyMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun Section(
    titleResId: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        LocalizedText(
            resId = titleResId,
            style = AppTextStyles.gummyMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFF424242),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(content = content)
    }
}