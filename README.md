# 📚 TetunDreams: 동티모르 어린이를 위한 다국어 동화책 앱
![app_mockup](/app/src/main/ic_launcher-playstore.png)

## 프로젝트 소개
TetunDreams는 동티모르 어린이들의 독서 접근성 향상을 위한 다국어 동화책 앱입니다.
동티모르는 강원도와 비슷한 면적에 130만 명의 인구가 살고 있는 작은 나라로, 자국어인 테툼어로 된 어린이 도서가 매우 부족한 실정입니다.

현재 일부 NGO들이 영어나 한국어로 된 동화책을 구해와 현지 청년들이 자원봉사로 번역하고 이를 스티커 형태로 부착하는 방식을 사용하고 있습니다. 하지만 이는 비용과 시간이 많이 소요되는 비효율적인 방식입니다.

이러한 문제를 해결하고자 저작권이 만료되거나 무료로 공개된 동화책들을 디지털화하여 제공하는 무료 앱을 개발하게 되었습니다. 이를 통해 현지 어린이들이 시간과 장소에 구애받지 않고 모국어로 된 동화를 읽을 수 있게 되기를 기대합니다.

## 개발 환경
- 언어: Kotlin
- 최소 지원 Android API: 21 (Android 5.0)
- 주요 라이브러리
  - Jetpack Compose
  - Coil (이미지 로딩)
  - Accompanist (페이저)
  - Coroutines (비동기 처리)

## 주요 기능
### 1. epub 파싱 및 표시
- epub 파일에서 이미지와 텍스트 추출
- 콘텐츠 구조화 및 저장

### 2. 가로 모드 전용 UI
- 최적화된 가로 모드 책장 화면
- 좌우 분할 화면 책 읽기 지원
- 직관적인 페이지 스와이프

### 향후 계획
- 다국어 지원
  - 영어 (English)
  - 테툼어 (Tetun)
  - 포르투갈어 (Português)
  - 인도네시아어 (Bahasa Indonesia)
- 추가 기능
  - 글자 크기 조절
  - 다크모드
  - 즐겨찾기
  - 도서 분류 및 검색
- 오프라인 지원
  - 전체 콘텐츠 오프라인 저장
  - 효율적인 캐시 관리

## 프로젝트 구조
```
com.timor.kidsstory/
├── domain/                # 비즈니스 로직 계층
│   ├── model/            # 데이터 모델
│   │   ├── StoryPage
│   │   └── StoryResource
│   ├── usecase/          # 비즈니스 로직
│   │   └── ExtractEpubUseCase
│   └── util/             # 유틸리티
│       └── FileUtils
│
├── presentation/         # UI 계층
│   ├── bookshelf/       # 책장 화면 관련
│   │   ├── BookshelfScreen
│   │   ├── BookshelfState
│   │   └── BookshelfViewModel
│   ├── reader/          # 읽기 화면 관련
│   │   ├── StoryDetailScreen
│   │   └── ReaderViewModel
│   └── common/          # 공통 컴포넌트
│       └── MainScreen
│
└── ui.theme/            # 테마 관련
```

## 기술적 특징
- Clean Architecture 적용
- MVVM 패턴
- Jetpack Compose UI
- Coroutines 비동기 처리

## 버전 정보
### v1.0 (2025.02)
- epub 파일 파싱 및 표시
- 가로 모드 책장 UI
- 좌우 분할 화면 책 읽기
- 페이지 스와이프 기능

## 개발 일정
- 1차 개발: 2025.01.27 ~ 2025.02.02
  - epub 파일 파싱
  - 가로 모드 책장 UI
  - 좌우 분할 화면 책 읽기
  - 페이지 스와이프 구현

- 2차 개발: 2025.02.03 ~ 2025.02.09
  - 다국어 지원
  - 성능 최적화
  - UI/UX 개선

- 베타 테스트: 2025.02
- 정식 출시: 2025.02

## 기대 효과
### 사회적 가치
- 동티모르 어린이 독서 문화 발전
- 자국어 교육 자료 확보
- 지역 사회 교육 인프라 개선

### 실용적 가치
- 지속 가능한 도서 공급 체계 구축
- 디지털 격차 해소

## 라이선스
본 프로젝트는 Creative Commons Attribution 4.0 International License (CC BY 4.0)에 따라 원본 자료를 변형하여 2차 제작한 앱입니다.

- 원저작자: Enuma, Inc. & The Foundation SeeArt for Book Culture
- 라이선스: CC BY 4.0
- 원본 라이선스 링크: http://creativecommons.org/licenses/by/4.0/

© 2019 Enuma, Inc. & The Foundation SeeArt for Book Culture

## 문의
프로젝트에 대한 문의사항은 [Issues](https://github.com/choe-yujin/android-kids-story-app/issues)에 남겨주세요.