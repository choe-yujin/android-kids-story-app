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

/*
* 권한을 요청할 수 있는 고통 요청 로직
* */
@Composable
fun PermissionRequest(
    permission: String,
    onPermissionGranted: () -> Unit,    // 권한 승인
    onPermissionDenied: () -> Unit,     // 권한 거절
    onShowRationale: () -> Unit = {},       // 권한이 아직 허용되지 않았을때, 사용자에게권한이 필요한 이유 설명
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
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (!isGranted.value) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)) {
                onShowRationale()
            } else {
                permissionLauncher.launch(permission)
            }
        } else {
            onPermissionGranted()
        }
    }
}