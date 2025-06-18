# 📚 TaleTail: 동티모르 어린이를 위한 다국어 동화책 앱
![app_mockup](/app/src/main/ic_launcher-playstore.png)

## 프로젝트 소개
TaleTail은 동티모르 어린이들에게 테툼어로 된 디지털 동화책을 제공하는 무료 앱입니다. 
동티모르는 어린이 도서가 부족하고, 인터넷 환경도 제한적이기 때문에, 오프라인에서도 접근할 수 있도록 최적화된 앱을 기획했습니다. 
저작권이 공개되거나 만료된 동화책을 디지털화하여 제공하며, 리소스를 최소화하여 데이터 사용을 줄이고, 언제 어디서나 모국어로 동화를 읽을 수 있도록 돕습니다. 
이 앱은 현지 어린이들에게 교육적인 기회를 제공하고, 지속 가능한 도서 공급 체계를 구축하는 것을 목표로 합니다.

## 개발 환경
- 언어: Kotlin
- 최소 지원 Android API: 21 (Android 5.0)
- 주요 라이브러리
  - Jetpack Compose
  - Coil (이미지 로딩)
  - Accompanist (페이저)
  - Coroutines (비동기 처리)

## 프로젝트 구조
```
## Project Structure
```kotlin
com.timor.kidsstory/
├── KidsStoryApplication.kt        # Application class for DI
├── MainActivity.kt                # Main entry point
│
├── domain/                        # Domain Layer
│   ├── model/                     # Domain models
│   │   ├── NavigationDirection   # Page navigation enum
│   │   ├── Page                  # Page info model
│   │   ├── PageDetail           # Page detail model
│   │   ├── StoryDetail         # Story detail model
│   │   └── StoryResource       # Story resource model
│   │
│   ├── repository/               # Repository interfaces
│   │   └── StoryRepository     # Story data access interface
│   │
│   ├── usecase/                  # Business logic
│   │   ├── GetStoriesUseCase    # Get all stories
│   │   ├── GetStoryUseCase      # Get single story
│   │   └── PageNavigationUseCase # Handle page navigation
│   │
│   └── util/                     # Utilities
│       └── FileUtils            # File handling utilities
│
├── data/                         # Data Layer
│   └── repository/
│       └── StoryRepositoryImpl  # Story repository implementation
│
└── presentation/                 # Presentation Layer
    ├── bookshelf/               # Bookshelf feature
    │   ├── BookshelfScreen     # Bookshelf UI
    │   ├── BookshelfViewModel  # Bookshelf logic
    │   ├── components/
    │   │   └── BookCover      # Book cover component
    │   └── model/
    │       ├── BookCoverUiState
    │       └── BookshelfUiState
    │
    ├── reader/                  # Reader feature
    │   ├── ReaderScreen        # Reader UI
    │   ├── ReaderViewModel     # Reader logic
    │   ├── components/
    │   │   ├── PageContent    # Page content component
    │   │   ├── PageImageSection
    │   │   └── PageTextSection
    │   └── model/
    │       ├── ImageSectionUiState
    │       ├── PageUiState
    │       └── ReaderUiState
    │
    └── navigation/              # Navigation
        └── NavGraph            # Navigation configuration
```

## 기술적 특징
- Clean Architecture와 MVVM 패턴 적용
  - Domain Layer: 비즈니스 로직과 모델 정의
  - Data Layer: 데이터 접근과 저장소 구현
  - Presentation Layer: UI 상태 관리 및 화면 구성
- Jetpack Compose를 활용한 선언형 UI
  - 가로 모드 전용 레이아웃
  - 좌우 분할 화면 구현
  - HorizontalPager를 활용한 페이지 스와이프
- Kotlin Coroutines & Flow
  - 비동기 작업 처리
  - 반응형 상태 관리
- 주요 라이브러리
  - Coil: 이미지 로딩
  - Accompanist: 페이저 기능
  - Navigation Compose: 화면 전환

## 개발 계획

### Phase 1: 기본 기능 구현 (v1.0) - 2025.01.27 ~ 2025.01.31
- Clean Architecture 기반 프로젝트 구조 설계
- 핵심 기능 구현
  - epub 파일 파싱 엔진 개발
  - 가로 모드 최적화 책장 UI
  - 좌우 분할 화면 책 읽기
  - 페이지 스와이프 네비게이션
- 알파 테스트 (2025.01.31)
  - 내부 테스터 대상 기능 검증
  - 성능 측정

### Phase 2: 기능 고도화 (v1.1) - 2025.02.01 ~ 2025.02.14
- Repository 계층 보강
  - UserPreferences/Bookmark Repository 구현
  - Room Database 도입
  - 캐시 메커니즘 구현
- 품질 개선
  - Hilt 도입
  - 테스트 코드 추가
  - 상태 관리 개선
- 베타 테스트
  - 외부 테스터 대상 사용성 테스트
  - 피드백 수집 및 반영

### Phase 3: 다국어 지원 (v1.2) - 2025 Q2
- 다국어 지원 시스템
  - 다국어 전환 UI
- 오프라인 모드 강화
  - 콘텐츠 다운로드 관리
    - 앱 설치 시 전체 콘텐츠 포함
    - 콘텐츠 업데이트시 사용자가 선택한 동화만 다운로드
    - 백그라운드 다운로드 지원
    - 다운로드 진행률 표시
  - 사용자 설정 로컬 저장
    - 마지막 읽은 페이지
    - 즐겨찾기/책갈피
    - 글자 크기 등 UI 설정

### Phase 4: 학습 요소 (v2.0) - 2025 Q3
- 교육 기능 추가
  - 독서 진도 추적
  - 이해도 체크 퀴즈

### Phase 5: 콘텐츠 확장 (v2.1) - 2025 Q4
- 오디오북 기능
- 인터랙티브 요소(옵션)
  - 읽기 보조 기능
    - 단어 터치시 발음/뜻 표시
    - 문장 하이라이팅
    - 읽기 진행률 시각화
  - 시/청각적 효과
    - 페이지 넘김 애니메이션
    - 간단한 캐릭터 애니메이션
    - 효과음

### 장기 계획 (2026년 이후)
- 플랫폼 확장 (iOS, 웹)
- AI 기능 통합
- 글로벌 서비스 확장

## Contributing to the Project
TaleTail은 오픈소스 프로젝트로, 여러분의 기여를 환영합니다!
TaleTail is an open-source project, and we welcome your contributions!

### How to Contribute
1. Fork the project
2. Create a feature branch (git checkout -b feature/AmazingFeature)
3. Commit your changes (git commit -m 'feat: Add some AmazingFeature')
4. Push the branch (git push origin feature/AmazingFeature)
5. Create a Pull Request

## Participate in Translation
TaleTail needs your help to gift digital storybooks to children in Timor-Leste!
Access the [translation](https://docs.google.com/spreadsheets/d/15ATv7NLafQUndAMp1R-HBEpaMbJypPLAOKNLtq5IrRM/edit?gid=1679690950#gid=1679690950) project spreadsheet to join.

### Current Contributors
- Son Ji-Young - Korean/Tetum translation
- Francisco Mendosa - English/Tetum translation

## Contact Us
### General Inquiries / Bug Reports
- GitHub Issues: [Project Issues Page](https://github.com/choe-yujin/android-kids-story-app/issues)
- Email: dev.yujinchoe@gmail.com

### Translation Inquiries
- Use the comment feature within the spreadsheet
- Contact us directly via email

## License
This project is an app created by modifying the original material under the Creative Commons Attribution 4.0 International License (CC BY 4.0).

- Original authors: Enuma, Inc. & The Foundation SeeArt for Book Culture
- License: CC BY 4.0
- Original license link: http://creativecommons.org/licenses/by/4.0/

© 2019 Enuma, Inc. & The Foundation SeeArt for Book Culture
