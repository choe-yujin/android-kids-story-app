package com.timor.kidsstory.presentation.leveltest.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
// 🆕 ripple effect 제거를 위한 import 추가
import androidx.compose.foundation.interaction.MutableInteractionSource

/**
 * 레벨 테스트 선택지 컴포넌트
 */
@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    showResult: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        showResult && isCorrect == true -> Color(0xFF4CAF50) // 정답: 초록색
        showResult && isCorrect == false && isSelected -> Color(0xFFF44336) // 오답: 빨간색
        isSelected && !showResult -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> Color.White
    }
    
    val borderColor = when {
        showResult && isCorrect == true -> Color(0xFF4CAF50)
        showResult && isCorrect == false && isSelected -> Color(0xFFF44336)
        isSelected && !showResult -> MaterialTheme.colorScheme.primary
        else -> Color(0xFFE0E0E0)
    }
    
    val textColor = when {
        showResult && (isCorrect == true || (isCorrect == false && isSelected)) -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = !showResult,
                indication = null, // 🔧 null을 사용하여 Ripple effect 제거
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

/**
 * 문제 텍스트 컴포넌트
 */
@Composable
fun QuestionText(
    text: String,
    questionNumber: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Question $questionNumber",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 26.sp
            )
        }
    }
}

/**
 * 레벨 표시 컴포넌트
 */
@Composable
fun LevelIndicator(
    currentLevel: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = "Level $currentLevel",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
