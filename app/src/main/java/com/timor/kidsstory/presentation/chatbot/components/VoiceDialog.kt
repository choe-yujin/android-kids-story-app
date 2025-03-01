package com.timor.kidsstory.presentation.chatbot.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.timor.kidsstory.presentation.chatbot.ChatbotScreenViewModel
import com.timor.kidsstory.presentation.chatbot.VoiceRecognitionState

//@Composable
//fun VoiceRecognitionDialog(viewModel: ChatbotScreenViewModel) {
//
//    // 음성 인식 다이얼로그
//    if (viewModel.state.value) {
//        AlertDialog(
//            onDismissRequest = { /* 음성 인식 중에 취소할 경우 처리 */ },
//            title = {
//                Text("음성 인식")
//            },
//            text = {
//                Text((voiceRecognitionState as VoiceRecognitionState.Listening).status)
//            },
//            confirmButton = {
//                TextButton(onClick = { viewModel.cancelVoiceSearch() }) {
//                    Text("취소")
//                }
//            }
//        )
//    }
//
//    // 음성 인식이 끝난 후 결과 표시
//    if (voiceRecognitionState is VoiceRecognitionState.Done) {
//        val result = (voiceRecognitionState as VoiceRecognitionState.Done).recognizedText
//        AlertDialog(
//            onDismissRequest = { /* 다이얼로그 닫기 */ },
//            title = {
//                Text("음성 인식 완료")
//            },
//            text = {
//                Text(result)
//            },
//            confirmButton = {
//                TextButton(onClick = { /* 결과 처리 후 다이얼로그 닫기 */ }) {
//                    Text("확인")
//                }
//            }
//        )
//    }
//}

