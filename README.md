# 📚 TetunDreams: 동티모르 어린이를 위한 다국어 동화책 앱
![app_mockup](/app/src/main/ic_launcher-playstore.png)

## 프로젝트 소개
TetunDreams는 동티모르 어린이들의 독서 접근성 향상을 위한 다국어 동화책 앱입니다.
동티모르는 강원도와 비슷한 면적에 130만 명의 인구가 살고 있는 작은 나라로, 자국어인 테툼어로 된 어린이 도서가 매우 부족한 실정입니다.

현재 일부 NGO들이 영어나 한국어로 된 동화책을 구해와 현지 청년들이 자원봉사로 번역하고 이를 스티커 형태로 부착하는 방식을 사용하고 있습니다. 하지만 이는 비용과 시간이 많이 소요되는 비효율적인 방식입니다.

이러한 문제를 해결하고자 저작권이 만료되거나 무료로 공개된 동화책들을 디지털화하여 제공하는 무료 앱을 개발하게 되었습니다. 이를 통해 현지 어린이들이 시간과 장소에 구애받지 않고 모국어로 된 동화를 읽을 수 있게 되기를 기대합니다.

## 개발 환경
- 언어: Kotlin
- 프레임워크: Android Jetpack Compose
- 데이터베이스: Room
- 개발 도구: Android Studio
- 최소 지원 Android API: 21 (Android 5.0)

## 주요 기능
### 1. 다국어 지원
- 영어 (English)
- 테툼어 (Tetun)
- 포르투갈어 (Português)
- 인도네시아어 (Bahasa Indonesia)

### 2. 오프라인 지원
- 전체 콘텐츠 오프라인 저장
- 최소한의 데이터 사용
- 효율적인 캐시 관리

### 3. 독서 기능
- 마지막 읽은 페이지 저장
- 글자 크기 조절
- 다크모드 지원
- 즐겨찾기
- 도서 분류 및 검색

## 프로젝트 구조
```
├── app
│   ├── data
│   │   ├── local
│   │   │   ├── dao
│   │   │   └── database
│   │   └── repository
│   ├── domain
│   │   ├── model
│   │   └── usecase
│   └── presentation
│       ├── bookshelf
│       ├── reader
│       └── common
└── build.gradle.kts
```

## 기술적 특징
- Clean Architecture 적용
- MVVM 패턴
- Repository 패턴
- Room Database
- WorkManager를 통한 다운로드 관리
- Offline-First 접근

## 기대 효과
### 사회적 가치
- 동티모르 어린이 독서 문화 발전
- 자국어 교육 자료 확보
- 지역 사회 교육 인프라 개선

### 실용적 가치
- 지속 가능한 도서 공급 체계 구축
- 디지털 격차 해소

## 개발 일정
- 1차 개발: 2025.01.27 ~ 2025.02.02
    - 기본 UI/UX 구현
    - 도서 뷰어 개발
    - 오프라인 지원

- 2차 개발: 2025.02.03 ~ 2025.02.09
    - 다국어 지원
    - 성능 최적화

- 베타 테스트: 2025.02
- 정식 출시: 2025.02

## 라이선스
본 프로젝트는 Creative Commons Attribution 4.0 International License (CC BY 4.0)에 따라 원본 자료를 변형하여 2차 제작한 앱입니다.

- 원저작자: Enuma, Inc. & The Foundation SeeArt for Book Culture
- 라이선스: CC BY 4.0
- 원본 라이선스 링크: http://creativecommons.org/licenses/by/4.0/

© 2019 Enuma, Inc. & The Foundation SeeArt for Book Culture

## 문의
프로젝트에 대한 문의사항은 [Issues](https://github.com/choe-yujin/android-kids-story-app/issues)에 남겨주세요.