package com.timor.kidsstory.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 설정 화면 UI 컴포넌트
 * - 앱 설정, 개발자 정보, 라이센스 등 표시
 * - 배경 음악 설정 제어
 *
 * @param state 설정 화면 UI 상태
 * @param onAction 사용자 액션 처리 콜백
 */
@Composable
fun SettingScreen(
    state: SettingUiState,
    onAction: (SettingAction) -> Unit,
) {
    // 메인 컨테이너
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.primary50)
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 헤더 부분 - 뒤로가기 버튼과 로고 포함
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(AppColors.primary300)
                    .padding(horizontal = 48.dp)
            ) {
                // 뒤로가기 (닫기) 버튼
                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = {
                        onAction(SettingAction.BackButtonClick)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // 앱 로고
                Icon(
                    painter = painterResource(id = R.drawable.info_logo_test),
                    contentDescription = "Info",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(60.dp),
                    tint = Color.Unspecified
                )
            }

            // 카드 영역 - 설정, 제작자 정보, 앱 정보 카드 배치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // 카드 사이 간격
            ) {
                // 설정 카드 - 음악 스위치 포함
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.9f), // 고정된 가로세로 비율
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // 카드 제목
                        LocalizedText(
                            resId = R.string.info_setting,
                            style = AppTextStyles.gummyMediumSemibold,
                            color = AppColors.neutral800,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // 음악 설정 토글
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 0.dp), // 패딩 보류
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LocalizedText(
                                resId = R.string.info_music,
                                style = AppTextStyles.gummySmallSemibold,
                                color = AppColors.neutral700
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // 커스텀 토글 스위치
                            CustomToggle(
                                isChecked = state.isMusicOn,
                                onToggle = { isMusicOn ->
                                    onAction(SettingAction.SwitchClick(isMusicOn))
                                }
                            )
                        }
                    }
                }

                // 제작자 카드 - 개발자 정보 표시
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.9f), // 고정된 가로세로 비율
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // 카드 제목
                        LocalizedText(
                            resId = R.string.info_created_by,
                            style = AppTextStyles.gummyMediumSemibold,
                            color = AppColors.neutral800,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // 개발자 목록
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DeveloperRow(
                                roleResId = R.string.info_dev_yujin,
                                flagResId = R.drawable.flag_ko
                            )
                            DeveloperRow(
                                roleResId = R.string.info_dev_jaeyeon,
                                flagResId = R.drawable.flag_ko
                            )
                            DeveloperRow(
                                roleResId = R.string.info_des_jinsung,
                                flagResId = R.drawable.flag_ko
                            )
                        }
                    }
                }

                // 정보 카드 - 앱 정보 및 라이센스 표시
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.9f), // 고정된 가로세로 비율
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // 카드 제목
                        LocalizedText(
                            resId = R.string.info_about,
                            style = AppTextStyles.gummyMediumSemibold,
                            color = AppColors.neutral800,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // 앱 정보 박스
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.blue50)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 앱이름 왼쪽, 버전 정보 오른쪽
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 왼쪽 섹션 - TaleTail 앱 이름
                                LocalizedText(
                                    resId = R.string.app_name,
                                    style = AppTextStyles.gummyVSmallMediumItalic,
                                    color = AppColors.blue700,
                                    modifier = Modifier.weight(1f)
                                )

                                // 오른쪽 섹션 - 버전 및 날짜
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LocalizedText(
                                            resId = R.string.info_version,
                                            style = AppTextStyles.gummyVvSmallRegularItalic,
                                            color = AppColors.neutral800,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = ": 1.0.1",
                                            style = AppTextStyles.gummyVvSmallRegularItalic,
                                            color = AppColors.neutral800,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Text(
                                        text = "2025-04-28",
                                        style = AppTextStyles.gummyVvSmallRegularItalic,
                                        color = AppColors.neutral600,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // 라이센스 정보
                        LocalizedText(
                            resId = R.string.info_license,
                            style = AppTextStyles.gummyMediumSemibold,
                            color = AppColors.neutral800,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                        Text(
                            text = "These books are licensed under CC BY 4.0 by Enuma, Inc. & The Foundation SeeArt for Book Culture. To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.",
                            style = AppTextStyles.pretendardVSmall,
                            color = AppColors.neutral600,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "ⓒ 2019 by Enuma, Inc. & The Foundation SeeArt for Book Culture",
                            style = AppTextStyles.pretendardVSmall,
                            color = AppColors.neutral700,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // 아래 여백 추가
        }
    }
}

/**
 * 커스텀 토글 스위치 컴포넌트
 */
@Composable
private fun CustomToggle(
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isChecked) AppColors.primary300 else Color.LightGray)
            .clickable { onToggle(!isChecked) }
            .padding(horizontal = 6.dp, vertical = 4.dp), // 바깥쪽 패딩
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isChecked) {
                LocalizedText(
                    resId = R.string.info_on,
                    style = AppTextStyles.gummySmallSemibold,
                    color = AppColors.primary800,
                    modifier = Modifier.padding(horizontal = 4.dp) // 켜기 양쪽 패딩
                )
            }

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            if (!isChecked) {
                LocalizedText(
                    resId = R.string.info_off,
                    style = AppTextStyles.gummySmallSemibold,
                    color = AppColors.neutral400,
                    modifier = Modifier.padding(horizontal = 4.dp) // 끄기 양쪽 패딩
                )
            }
        }
    }
}

/**
 * 개발자 정보를 표시하는 행 컴포넌트
 */
@Composable
private fun DeveloperRow(roleResId: Int, flagResId: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.neutral100)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LocalizedText(
                resId = roleResId,
                style = AppTextStyles.gummyVSmallMediumItalic,
                color = AppColors.neutral800,
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = flagResId),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun SettingScreenPreview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = false),
            onAction = {}
        )
    }
}