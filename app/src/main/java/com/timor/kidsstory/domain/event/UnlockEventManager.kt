package com.timor.kidsstory.domain.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 잠금 해제 이벤트를 전역적으로 관리하는 싱글톤 객체
 */
object UnlockEventManager {
    private val _unlockEvent = MutableSharedFlow<String>() // 잠금 해제된 레벨 그룹 (예: "level_1")
    val unlockEvent = _unlockEvent.asSharedFlow()

    suspend fun postUnlockEvent(levelGroup: String) {
        _unlockEvent.emit(levelGroup)
    }
}
