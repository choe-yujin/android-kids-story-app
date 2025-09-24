package com.timor.kidsstory.presentation.bookshelf.model

import com.timor.kidsstory.R

/**
 * 관리 모드에서 사용되는 탭 종류
 */
enum class ManagementTab(val titleResId: Int) {
    /**
     * 다운로드 탭 - 다운로드 가능한 새로운 책들만
     */
    DOWNLOAD(R.string.management_tab_download),
    
    /**
     * 업데이트 탭 - 업데이트 가능한 기존 책들만
     */
    UPDATE(R.string.management_tab_update),
    
    /**
     * 삭제 탭 - 로컬에 저장된 책들만
     */
    DELETE(R.string.management_tab_delete)
}
