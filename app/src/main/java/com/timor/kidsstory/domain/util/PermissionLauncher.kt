package com.timor.kidsstory.domain.util

import android.app.Activity
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * 권한 요청 컴포저블
 *
 * Jetpack Compose 환경에서 안드로이드 권한을 요청하고 처리하는 재사용 가능한 컴포넌트입니다.
 * 이 컴포넌트는 권한 확인, 요청, 결과 처리를 자동화하여 앱의 권한 관리를 간소화합니다.
 *
 * 주요 기능:
 * - 현재 권한 상태 확인
 * - 사용자에게 권한 요청 다이얼로그 표시
 * - 권한 요청 결과에 따른 콜백 처리
 * - 필요시 권한 요청 근거(rationale) 표시 지원
 *
 * 사용 예시:
 * PermissionRequest(
 *     permission = Manifest.permission.RECORD_AUDIO,
 *     onPermissionGranted = { startRecording() },
 *     onPermissionDenied = { showErrorMessage() },
 *     onShowRationale = { showWhyWeNeedPermission() }
 * )
 *
 * @param permission 요청할 권한 문자열 (ex: Manifest.permission.CAMERA)
 * @param onPermissionGranted 권한이 승인되었을 때 실행할 콜백
 * @param onPermissionDenied 권한이 거부되었을 때 실행할 콜백
 * @param onShowRationale 권한 요청 근거를 표시해야 할 때 실행할 콜백 (선택적)
 */
@Composable
fun PermissionRequest(
    permission: String,
    onPermissionGranted: () -> Unit,  // 권한 승인
    onPermissionDenied: () -> Unit,  // 권한 거절
    onShowRationale: () -> Unit = {},  // 권한이 아직 허용되지 않았을때, 사용자에게권한이 필요한 이유 설명
) {
    val context = LocalContext.current

    // 현재 권한 상태 확인
    val isGranted = remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted.value = granted
        if (granted) {
            // 권한이 승인되면 onPermissionGranted 콜백 실행
            onPermissionGranted()
        } else {
            // 권한이 거부되면 onPermissionDenied 콜백 실행
            onPermissionDenied()
        }
    }

    // 컴포저블이 첫 실행될 때 권한 상태 확인 및 필요시 요청
    LaunchedEffect(Unit) {
        // 권한이 승인되지 않은 상태
        if (!isGranted.value) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)) {
                // 사용자가 이전에 권한을 거부했으며, 권한이 필요한 이유를 설명해야 함
                onShowRationale()
            } else {
                // 권한 요청 다이얼로그 표시
                permissionLauncher.launch(permission)
            }
        } else {
            // 이미 권한이 승인된 경우
            onPermissionGranted()
        }
    }
}