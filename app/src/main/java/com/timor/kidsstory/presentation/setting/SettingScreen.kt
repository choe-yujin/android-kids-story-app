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
                        Text(
                            text = stringResource(R.string.info_setting),
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
                            Text(
                                text = stringResource(R.string.info_music),
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
                        Text(
                            text = stringResource(R.string.info_created_by),
                            style = AppTextStyles.gummyMediumSemibold,
                            color = AppColors.neutral800,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // 개발자 목록
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DeveloperRow("Dev/", "Yujin Choe", R.drawable.flag_ko)
                            DeveloperRow("Dev/", "JaeYeon Kim", R.drawable.flag_ko)
                            DeveloperRow("Des/", "Jinsung Kuak", R.drawable.flag_ko)
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
                        Text(
                            text = stringResource(R.string.info_about),
                            style = AppTextStyles.gummyMediumSemibold,
                            color = AppColors.neutral800,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // 앱 정보 박스
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                //.height(48.dp)
                                .background(AppColors.blue50)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 앱이름 왼쪽, 버전 정보 오른쪽
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 왼쪽 섹션 - TaleTail 앱 이름
                                Text(
                                    text = "TaleTail",
                                    style = AppTextStyles.gummyVSmallMediumItalic,
                                    color = AppColors.blue700,
                                    modifier = Modifier.weight(1f)
                                )

                                // 오른쪽 섹션 - 버전 및 날짜
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "version: 1.0.0",
                                        style = AppTextStyles.gummyVvSmallRegularItalic,
                                        color = AppColors.neutral800,
                                        fontSize = 12.sp
                                    )

                                    Text(
                                        text = "2025-03-04",
                                        style = AppTextStyles.gummyVvSmallRegularItalic,
                                        color = AppColors.neutral600,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // 라이센스 정보
                        Text(
                            text = stringResource(R.string.info_license),
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
 * - 음악 켜기/끄기 기능 제공
 *
 * @param isChecked 현재 토글 상태
 * @param onToggle 토글 상태 변경 콜백
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
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(64.dp) // 토글 고정 너비
        ) {
            if (isChecked) {
                Text(
                    text = stringResource(R.string.info_on),
                    style = AppTextStyles.gummySmallSemibold,
                    color = AppColors.primary800,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            // 흰색 동그라미 핸들
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            if (!isChecked) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.info_off),
                    style = AppTextStyles.gummySmallSemibold,
                    color = AppColors.neutral400,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

/**
 * 개발자 정보를 표시하는 행 컴포넌트
 * - 역할, 이름, 국가 플래그를 함께 표시
 *
 * @param role 역할 정보 (예: "Dev/", "Des/")
 * @param name 개발자 이름
 * @param flagResId 국가 플래그 리소스 ID
 */
@Composable
private fun DeveloperRow(role: String, name: String, flagResId: Int) {
    // 회색 배경 박스
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
            // 역할 텍스트
            Text(
                text = role,
                style = AppTextStyles.gummyVvSmallRegularItalic,
                color = AppColors.neutral600
            )

            // 이름 텍스트
            Text(
                text = name,
                style = AppTextStyles.gummyVSmallMediumItalic,
                color = AppColors.neutral800,
            )

            Spacer(modifier = Modifier.weight(1f))

            // 국가 플래그 아이콘
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