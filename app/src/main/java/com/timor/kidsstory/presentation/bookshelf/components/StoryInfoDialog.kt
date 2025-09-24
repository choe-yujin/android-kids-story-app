package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.StoryInfo
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles

/**
 * 동화 정보를 보여주는 다이얼로그
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
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // 제목
                LocalizedText(
                    resId = R.string.story_info_dialog_title,
                    style = AppTextStyles.gummyLgSemiboldItalic,
                    color = AppColors.neutral900,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 스크롤 가능한 콘텐츠
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 줄거리
                    if (!storyInfo.summary.isNullOrBlank()) {
                        LocalizedText(
                            resId = R.string.story_summary_label,
                            style = AppTextStyles.gummyLgSemiboldItalic,
                            color = AppColors.blue500,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = storyInfo.summary,
                            style = AppTextStyles.gummyLgSemiboldItalic,
                            color = AppColors.neutral700,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    } else {
                        LocalizedText(
                            resId = R.string.story_summary_label,
                            style = AppTextStyles.gummyLgSemiboldItalic,
                            color = AppColors.blue500,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        LocalizedText(
                            resId = R.string.no_summary_available,
                            style = AppTextStyles.gummyLgSemiboldItalic,
                            color = AppColors.neutral400,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }
                    
                    // 사전 질문
                    LocalizedText(
                        resId = R.string.pre_questions_label,
                        style = AppTextStyles.gummyLgSemiboldItalic,
                        color = AppColors.green500,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    if (storyInfo.preQuestions.isNotEmpty()) {
                        storyInfo.preQuestions.forEachIndexed { index, question ->
                            Text(
                                text = "${index + 1}. $question",
                                style = AppTextStyles.gummyLgSemiboldItalic,
                                color = AppColors.neutral700,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    } else {
                        LocalizedText(
                            resId = R.string.no_pre_questions_available,
                            style = AppTextStyles.gummyLgSemiboldItalic,
                            color = AppColors.neutral400,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 사후 질문
                    LocalizedText(
                        resId = R.string.post_questions_label,
                        style = AppTextStyles.gummyLgSemiboldItalic,
                        color = AppColors.yellowRed500,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    if (storyInfo.postQuestions.isNotEmpty()) {
                        storyInfo.postQuestions.forEachIndexed { index, question ->
                            Text(
                                text = "${index + 1}. $question",
                                style = AppTextStyles.gummyLgSemiboldItalic,
                                color = AppColors.neutral700,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    } else {
                        LocalizedText(
                            resId = R.string.no_post_questions_available,
                            style = AppTextStyles.gummyLgSemiboldItalic,
                            color = AppColors.neutral400
                        )
                    }
                }
                
                // 닫기 버튼
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.blue500
                    )
                ) {
                    LocalizedText(
                        resId = R.string.close_button,
                        style = AppTextStyles.gummyLgSemiboldItalic,
                        color = AppColors.neutralWhite
                    )
                }
            }
        }
    }
}