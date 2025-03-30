package com.timor.kidsstory.domain.usecase.preference

import com.timor.kidsstory.domain.model.UserPreference
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 사용자 설정을 가져오는 UseCase
 * - 사용자 설정 정보를 저장소에서 읽어와 제공
 * - 설정 변경 사항을 실시간으로 관찰 가능한 Flow 반환
 *
 * @property repository 사용자 설정 저장소
 */
class GetUserPreferenceUseCase @Inject constructor(
    private val repository: UserPreferenceRepository
) {

    /**
     * 사용자 설정을 Flow로 제공
     * - 설정 변경 시 자동으로 새 값 방출
     * - 호출 방식: userPreferenceUseCase()
     *
     * @return 사용자 설정 Flow
     */
    operator fun invoke(): Flow<UserPreference> {
        return repository.getUserPreferences()
    }
}