package com.timor.kidsstory.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

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
    // 반응형 크기 계산
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    
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
                    .height(ResponsiveTextUtils.getHeaderHeight().dp) // BookshelfHeader와 동일한 헤더 높이
                    .background(AppColors.primary300)

            ) {
                // 뒤로가기 (닫기) 버튼 - 책장 화면 톱니바퀴와 동일한 위치
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 48.dp) // 책장 화면과 동일한 패딩
                        .size(ResponsiveTextUtils.getHeaderIconSize().dp) // BookshelfHeader의 아이콘과 동일한 크기
                        .clickable {
                            onAction(SettingAction.BackButtonClick)
                        }
                )

                // 앱 로고
                Icon(
                    painter = painterResource(id = R.drawable.info_logo_test),
                    contentDescription = "Info",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size((48 * ResponsiveTextUtils.getScreenScaleFactor()).dp), // BookshelfHeader 로고 크기에 비례하도록 조정
                    tint = Color.Unspecified
                )
            }

            // 카드 영역 - 설정, 제작자 정보, 앱 정보 카드 배치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 남은 공간을 모두 차지하도록 변경
                    .padding(
                        horizontal = 48.dp, // 책장 화면과 동일한 패딩
                        vertical = (16 * scaleFactor).dp // 세로 간격 조정
                    ),
                horizontalArrangement = Arrangement.spacedBy((16 * scaleFactor).dp)
            ) {
                // 설정 카드 - 음악 스위치 포함
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(), // 높이를 전체로 채우도록 변경
                    shape = RoundedCornerShape((16 * scaleFactor).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = (4 * scaleFactor).dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding((16 * scaleFactor).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 카드 제목
                        LocalizedText(
                            resId = R.string.info_setting,
                            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
                            color = AppColors.neutral800
                        )
                        
                        Spacer(modifier = Modifier.height((12 * scaleFactor).dp))

                        // 음악 설정 토글 (제목 바로 아래)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LocalizedText(
                                resId = R.string.info_music,
                                style = ResponsiveTextUtils.getSettingTextStyle(),
                                color = AppColors.neutral700
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // 커스텀 토글 스위치
                            CustomToggle(
                                isChecked = state.isMusicOn,
                                onToggle = { isMusicOn ->
                                    onAction(SettingAction.MusicSwitchClick(isMusicOn))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height((4 * scaleFactor).dp))

                        // 배경음악 음량 조절 (토글 바로 아래)
                        VolumeControlButtonsOnly(
                            volume = state.musicVolume,
                            enabled = state.isMusicOn,
                            onVolumeChange = { volume ->
                                onAction(SettingAction.MusicVolumeChange(volume))
                            }
                        )

                        Spacer(modifier = Modifier.height((8 * scaleFactor).dp))

                        // 효과음 설정 토글
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LocalizedText(
                                resId = R.string.info_sound_effect,
                                style = ResponsiveTextUtils.getSettingTextStyle(),
                                color = AppColors.neutral700
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // 커스텀 토글 스위치
                            CustomToggle(
                                isChecked = state.isSoundEffectOn,
                                onToggle = { isSoundEffectOn ->
                                    onAction(SettingAction.SoundEffectSwitchClick(isSoundEffectOn))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height((4 * scaleFactor).dp))

                        // 효과음 음량 조절 (토글 바로 아래)
                        VolumeControlButtonsOnly(
                            volume = state.soundEffectVolume,
                            enabled = state.isSoundEffectOn,
                            onVolumeChange = { volume ->
                                onAction(SettingAction.SoundEffectVolumeChange(volume))
                            }
                        )
                    }
                }

                // 제작자 카드 - 개발자 정보 표시
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(), // 높이를 전체로 채우도록 변경
                    shape = RoundedCornerShape((16 * scaleFactor).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = (4 * scaleFactor).dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding((16 * scaleFactor).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 카드 제목
                        LocalizedText(
                            resId = R.string.info_created_by,
                            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
                            color = AppColors.neutral800
                        )
                        
                        Spacer(modifier = Modifier.height((12 * scaleFactor).dp))

                        // 개발자 목록 (제목 바로 아래)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy((6 * scaleFactor).dp)
                        ) {
                            DeveloperRow(
                                roleResId = R.string.info_dev_yujin,
                                flagResId = R.drawable.flag_ko,
                                hasEmail = true,
                                onEmailClick = { onAction(SettingAction.EmailIconClick) }
                            )
                            DeveloperRow(
                                roleResId = R.string.info_dev_jaeyeon,
                                flagResId = R.drawable.flag_ko,
                                hasEmail = false,
                                onEmailClick = {}
                            )
                            DeveloperRow(
                                roleResId = R.string.info_des_jinsung,
                                flagResId = R.drawable.flag_ko,
                                hasEmail = false,
                                onEmailClick = {}
                            )
                            DeveloperRow(
                                roleResId = R.string.info_des_yuni,
                                flagResId = R.drawable.flag_ko,
                                hasEmail = false,
                                onEmailClick = {}
                            )
                        }
                    }
                }

                // 정보 카드 - 앱 정보 및 라이센스 표시
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(), // 높이를 전체로 채우도록 변경
                    shape = RoundedCornerShape((16 * scaleFactor).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = (4 * scaleFactor).dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding((16 * scaleFactor).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 카드 제목
                        LocalizedText(
                            resId = R.string.info_about,
                            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
                            color = AppColors.neutral800
                        )
                        
                        Spacer(modifier = Modifier.height((12 * scaleFactor).dp))

                            // 앱 정보 박스 (위아래 여백 제거)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape((8 * scaleFactor).dp))
                                    .background(AppColors.blue50)
                                    .padding(horizontal = (12 * scaleFactor).dp, vertical = (8 * scaleFactor).dp), // 상하 패딩 추가
                                contentAlignment = Alignment.Center
                            ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween // 양쪽 끝으로 나눌
                            ) {
                                // 왼쪽 섹션 - TaleTail 앱 이름
                                LocalizedText(
                                    resId = R.string.app_name,
                                    style = ResponsiveTextUtils.getSettingSmallTextStyle(),
                                    color = AppColors.blue700
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
                                            style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                                            color = AppColors.neutral800
                                        )
                                        Text(
                                            text = ": 1.0.1",
                                            style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                                            color = AppColors.neutral800
                                        )
                                    }

                                    Text(
                                        text = "2025-06-24",
                                        style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                                        color = AppColors.neutral600
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height((12 * scaleFactor).dp))

                        // 라이센스 정보 섹션 (버전 박스 바로 아래)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LocalizedText(
                                resId = R.string.info_license,
                                style = ResponsiveTextUtils.getSettingCardTitleStyle(),
                                color = AppColors.neutral800,
                                modifier = Modifier.padding(bottom = (6 * scaleFactor).dp)
                            )
                            Text(
                                text = "These books are licensed under CC BY 4.0 by Enuma, Inc. & The Foundation SeeArt for Book Culture. To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.",
                                style = ResponsiveTextUtils.getLicenseTextStyle(), // 라이센스 전용 스타일 사용
                                color = AppColors.neutral600,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "ⓒ 2019 by Enuma, Inc. & The Foundation SeeArt for Book Culture",
                                style = ResponsiveTextUtils.getLicenseTextStyle(), // 라이센스 전용 스타일 사용
                                color = AppColors.neutral700,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        
        // 이메일 다이얼로그
        if (state.showEmailDialog) {
            EmailDialog(
                onDismiss = { onAction(SettingAction.DismissEmailDialog) },
                onWebsiteClick = { onAction(SettingAction.WebsiteLinkClick) }
            )
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
    val responsivePadding = (4 * ResponsiveTextUtils.getScreenScaleFactor()).dp // 패딩 줄임
    val responsiveInnerPadding = (3 * ResponsiveTextUtils.getScreenScaleFactor()).dp // 내부 패딩 줄임
    val toggleSize = (20 * ResponsiveTextUtils.getScreenScaleFactor()).dp // 토글 크기 줄임
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isChecked) AppColors.primary300 else Color.LightGray)
            .clickable { onToggle(!isChecked) }
            .padding(horizontal = responsivePadding, vertical = responsiveInnerPadding), // 바깥쪽 패딩
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isChecked) {
                LocalizedText(
                    resId = R.string.info_on,
                    style = ResponsiveTextUtils.getSettingToggleTextStyle(),
                    color = AppColors.primary800,
                    modifier = Modifier.padding(horizontal = responsiveInnerPadding) // 켜기 양쪽 패딩
                )
            }
            Box(
                modifier = Modifier
                    .size(toggleSize)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            if (!isChecked) {
                LocalizedText(
                    resId = R.string.info_off,
                    style = ResponsiveTextUtils.getSettingToggleTextStyle(),
                    color = AppColors.neutral400,
                    modifier = Modifier.padding(horizontal = responsiveInnerPadding) // 끄기 양쪽 패딩
                )
            }
        }
    }
}

/**
 * 이메일 다이얼로그 컴포넌트
 */
@Composable
private fun EmailDialog(
    onDismiss: () -> Unit,
    onWebsiteClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // X 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = AppColors.neutral600,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 이메일 주소
                Text(
                    text = "dev.yujinchoe@gmail.com",
                    style = AppTextStyles.pretendardLargeMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = AppColors.primary700
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 웹사이트 링크
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.primary50)
                        .clickable {
                            onWebsiteClick()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "www.taletail.shop",
                        style = AppTextStyles.pretendardMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            color = AppColors.primary600
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.width(6.dp))
                    
                    // 클릭 아이콘 (ic_click.svg)
                    Icon(
                        painter = painterResource(R.drawable.ic_click),
                        contentDescription = "Click to open website",
                        tint = AppColors.primary600,
                        modifier = Modifier.size(14.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {},
        dismissButton = {},
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * 개발자 정보를 표시하는 행 컴포넌트
 */
@Composable
private fun DeveloperRow(
    roleResId: Int, 
    flagResId: Int,
    hasEmail: Boolean = false,
    onEmailClick: () -> Unit = {}
) {
    val scaleFactor = ResponsiveTextUtils.getSettingScaleFactor()
    val iconSize = ResponsiveTextUtils.getResponsiveIconSize().dp
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape((8 * scaleFactor).dp))
            .background(AppColors.neutral100)
            .padding(
                horizontal = (10 * scaleFactor).dp, 
                vertical = (4 * scaleFactor).dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LocalizedText(
                resId = roleResId,
                style = ResponsiveTextUtils.getDeveloperNameTextStyle(), // 개발자 이름 전용 스타일 사용
                color = AppColors.neutral800,
            )

            Spacer(modifier = Modifier.weight(1f))

            // 이메일 아이콘 (최유진만 표시)
            if (hasEmail) {
                Card(
                    modifier = Modifier
                        .size(iconSize)
                        .clickable { onEmailClick() },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = AppColors.primary100),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = AppColors.primary300
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mail),
                            contentDescription = "Email",
                            modifier = Modifier.size((iconSize.value * 0.6f).dp),
                            tint = AppColors.primary600
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(
                painter = painterResource(id = flagResId),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = Color.Unspecified
            )
        }
    }
}

/**
 * 음량 조절 버튼만 표시하는 컴포넌트 (제목 없이)
 */
@Composable
private fun VolumeControlButtonsOnly(
    volume: Float,
    enabled: Boolean,
    onVolumeChange: (Float) -> Unit
) {
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    val buttonSize = (24 * scaleFactor).dp
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 빈 공간 (토글과 정렬을 위해)
        Spacer(modifier = Modifier.weight(1f))
        
        // 음량 조절 버튼 그룹
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 마이너스 버튼
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .clip(CircleShape)
                    .background(if (enabled) AppColors.primary300 else AppColors.neutral200)
                    .clickable(enabled = enabled) {
                        val newVolume = (volume - 0.1f).coerceAtLeast(0f)
                        onVolumeChange(newVolume)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "−",
                    style = ResponsiveTextUtils.getSettingTextStyle().copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = if (enabled) AppColors.primary800 else AppColors.neutral400
                )
            }

            Spacer(modifier = Modifier.width((8 * scaleFactor).dp))

            // 음량 표시
            Text(
                text = "${(volume * 100).toInt()}%",
                style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                color = if (enabled) AppColors.neutral700 else AppColors.neutral400,
                modifier = Modifier.width((32 * scaleFactor).dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width((8 * scaleFactor).dp))

            // 플러스 버튼
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .clip(CircleShape)
                    .background(if (enabled) AppColors.primary300 else AppColors.neutral200)
                    .clickable(enabled = enabled) {
                        val newVolume = (volume + 0.1f).coerceAtMost(1f)
                        onVolumeChange(newVolume)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    style = ResponsiveTextUtils.getSettingTextStyle().copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = if (enabled) AppColors.primary800 else AppColors.neutral400
                )
            }
        }
    }
}

/**
 * 음량 조절 행 컴포넌트 (제목 포함 - 하위 호환성을 위해 유지)
 */
@Composable
private fun VolumeControlRow(
    titleResId: Int,
    volume: Float,
    enabled: Boolean,
    onVolumeChange: (Float) -> Unit
) {
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    val buttonSize = (24 * scaleFactor).dp
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 제목 부분
        LocalizedText(
            resId = titleResId,
            style = ResponsiveTextUtils.getSettingTextStyle(),
            color = if (enabled) AppColors.neutral700 else AppColors.neutral400,
            modifier = Modifier.weight(1f) // 남은 공간 차지
        )
        
        // 음량 조절 버튼 그룹 (오른쪽)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 마이너스 버튼
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .clip(CircleShape)
                    .background(if (enabled) AppColors.primary300 else AppColors.neutral200)
                    .clickable(enabled = enabled) {
                        val newVolume = (volume - 0.1f).coerceAtLeast(0f)
                        onVolumeChange(newVolume)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "−",
                    style = ResponsiveTextUtils.getSettingTextStyle().copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = if (enabled) AppColors.primary800 else AppColors.neutral400
                )
            }

            Spacer(modifier = Modifier.width((8 * scaleFactor).dp))

            // 음량 표시
            Text(
                text = "${(volume * 100).toInt()}%",
                style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                color = if (enabled) AppColors.neutral700 else AppColors.neutral400,
                modifier = Modifier.width((32 * scaleFactor).dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width((8 * scaleFactor).dp))

            // 플러스 버튼
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .clip(CircleShape)
                    .background(if (enabled) AppColors.primary300 else AppColors.neutral200)
                    .clickable(enabled = enabled) {
                        val newVolume = (volume + 0.1f).coerceAtMost(1f)
                        onVolumeChange(newVolume)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    style = ResponsiveTextUtils.getSettingTextStyle().copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = if (enabled) AppColors.primary800 else AppColors.neutral400
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 160, device = "id:pixel_5")
@Composable
fun SettingScreenPixel5Preview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = true, isSoundEffectOn = true, musicVolume = 0.7f, soundEffectVolume = 0.5f),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 150)
@Composable
fun SettingScreenSmallPhonePreview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = false, isSoundEffectOn = true, musicVolume = 0.3f, soundEffectVolume = 0.8f),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 200)
@Composable
fun SettingScreenPhonePreview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = true, isSoundEffectOn = false, musicVolume = 0.6f, soundEffectVolume = 0.2f),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun SettingScreenPreview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = false, isSoundEffectOn = false, musicVolume = 0.0f, soundEffectVolume = 0.0f),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1024, heightDp = 600)
@Composable
fun SettingScreenTabletPreview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = true, isSoundEffectOn = true, musicVolume = 1.0f, soundEffectVolume = 0.9f),
            onAction = {}
        )
    }
}