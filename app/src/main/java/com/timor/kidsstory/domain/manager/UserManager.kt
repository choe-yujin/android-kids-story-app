package com.timor.kidsstory.domain.manager

import com.timor.kidsstory.data.local.database.dao.UserDao
import com.timor.kidsstory.data.local.database.entity.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 사용자 관리를 담당하는 Manager
 * - 현재는 단일 사용자 (default_user) 관리
 * - 향후 회원 시스템 도입 시 실제 사용자 ID로 확장 가능
 */
@Singleton
class UserManager @Inject constructor(
    private val userDao: UserDao
) {
    private val _currentUserId = MutableStateFlow("default_user")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    /**
     * 현재 사용자 ID 가져오기 (동기)
     */
    fun getCurrentUserId(): String = _currentUserId.value

    /**
     * 사용자 초기화 (앱 시작 시 호출)
     */
    suspend fun initializeUser() {
        // 기존 사용자가 있는지 확인
        var user = userDao.getUser("default_user")
        
        if (user == null) {
            // 새 사용자 생성
            user = UserEntity(
                userId = "default_user",
                username = "Guest",
                userMode = "OFFLINE",
                createdAt = System.currentTimeMillis(),
                lastActiveAt = System.currentTimeMillis()
            )
            userDao.insertUser(user)
        } else {
            // 마지막 활동 시간 업데이트
            userDao.updateLastActive("default_user", System.currentTimeMillis())
        }
        
        _currentUser.value = user
        _currentUserId.value = user.userId
    }

    /**
     * 사용자 모드 업데이트 (향후 온라인/오프라인 모드 전환용)
     */
    suspend fun updateUserMode(mode: String) {
        val user = _currentUser.value?.copy(
            userMode = mode,
            lastActiveAt = System.currentTimeMillis()
        ) ?: return
        
        userDao.insertUser(user)
        _currentUser.value = user
    }

    /**
     * 마지막 활동 시간 업데이트
     */
    suspend fun updateLastActive() {
        userDao.updateLastActive(getCurrentUserId(), System.currentTimeMillis())
    }
}
