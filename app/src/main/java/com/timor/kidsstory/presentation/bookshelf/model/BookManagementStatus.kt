package com.timor.kidsstory.presentation.bookshelf.model

/**
 * 관리 모드에서 책의 상태
 */
sealed class BookManagementStatus {
    /**
     * 다운로드 가능 - 아직 다운로드하지 않은 책
     */
    data object Available : BookManagementStatus()
    
    /**
     * 다운로드됨 - 이미 다운로드된 책 (삭제 가능)
     */
    data object Downloaded : BookManagementStatus()
    
    /**
     * 업데이트 가능 - 새 버전이 있는 책
     */
    data class UpdateAvailable(
        val currentVersion: Int,
        val newVersion: Int
    ) : BookManagementStatus()
    
    /**
     * 다운로드 중
     */
    data class Downloading(val progress: Float) : BookManagementStatus()
    
    /**
     * 업데이트 중
     */
    data class Updating(val progress: Float) : BookManagementStatus()
    
    /**
     * 잠김 - Unlock 조건 미충족
     */
    data class Locked(val requiredUnlockStep: Int) : BookManagementStatus()
}

/**
 * 관리 모드 작업 타입
 */
enum class ManagementActionType {
    DOWNLOAD,  // 다운로드
    UPDATE,    // 업데이트
    DELETE     // 삭제
}

/**
 * 관리 가능한 책 UI 모델
 */
data class ManageableBookUiModel(
    val bookId: String,
    val title: String,
    val coverImage: String,
    val level: Int,
    val status: BookManagementStatus,
    val size: Long, // bytes
    val isSelected: Boolean = false,
    val unlockStep: Int = 0
) {
    /**
     * 이 책에 대해 가능한 작업
     */
    val availableAction: ManagementActionType?
        get() = when (status) {
            is BookManagementStatus.Available -> ManagementActionType.DOWNLOAD
            is BookManagementStatus.Downloaded -> ManagementActionType.DELETE
            is BookManagementStatus.UpdateAvailable -> ManagementActionType.UPDATE
            is BookManagementStatus.Downloading,
            is BookManagementStatus.Updating,
            is BookManagementStatus.Locked -> null
        }
    
    /**
     * 선택 가능 여부
     */
    val isSelectable: Boolean
        get() = when (status) {
            is BookManagementStatus.Downloading,
            is BookManagementStatus.Updating,
            is BookManagementStatus.Locked -> false
            else -> true
        }
}
