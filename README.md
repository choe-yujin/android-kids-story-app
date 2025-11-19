# 📚 TaleTail: 동티모르 어린이를 위한 다국어 동화책 앱
![app_mockup](/app/src/main/ic_launcher-playstore.png)

## 프로젝트 소개
TaleTail은 동티모르 어린이들에게 테툼어로 된 디지털 동화책을 제공하는 무료 앱입니다.
동티모르는 어린이 도서가 부족하고, 인터넷 환경도 제한적이기 때문에, 오프라인에서도 접근할 수 있도록 최적화된 앱을 기획했습니다.
저작권이 공개되거나 만료된 동화책을 디지털화하여 제공하며, 리소스를 최소화하여 데이터 사용을 줄이고, 언제 어디서나 모국어로 동화를 읽을 수 있도록 돕습니다.
이 앱은 현지 어린이들에게 교육적인 기회를 제공하고, 지속 가능한 도서 공급 체계를 구축하는 것을 목표로 합니다.

## 개발 환경
- **언어**: Kotlin
- **최소 지원 Android API**: 21 (Android 5.0)
- **타겟 SDK**: 34 (Android 14)
- **아키텍처**: Clean Architecture + MVVM 패턴
- **주요 라이브러리**
  - Jetpack Compose (UI 프레임워크)
  - Hilt (의존성 주입)
  - Room (로컬 데이터베이스)
  - Ktor Client (네트워크 통신)
  - Coil (이미지 로딩)
  - Accompanist (페이저 기능)
  - Coroutines + Flow (비동기 처리)
  - WorkManager (백그라운드 다운로드)
  - TTS (Text-to-Speech)

## 주요 기능

### 📖 핵심 기능
- **오프라인 도서 열람**: 인터넷 연결 없이도 다운로드된 동화책 읽기
- **다국어 지원**: 테툼어, 한국어, 영어 지원
- **오디오 기능**: TTS(음성 합성)를 통한 동화 읽어주기
- **효과음 시스템**: 버튼 클릭음 및 페이지 넘김 효과음
- **책 다운로드**: GitHub에서 새로운 동화책 다운로드
- **카테고리 필터링**: 문화, 민담, 전설, 생활 등 주제별 분류
- **레벨별 필터링**: 연령대별 난이도 구분

### 🎨 사용자 경험
- **가로 모드 최적화**: 태블릿 친화적 레이아웃
- **좌우 분할 화면**: 이미지와 텍스트의 균형잡힌 배치
- **페이지 스와이프**: 직관적인 페이지 넘김
- **반응형 텍스트**: 화면 크기에 따른 텍스트 자동 조정
- **다크/라이트 테마**: 읽기 환경 최적화

## 프로젝트 구조
```
com.timor.kidsstory/
├── TaleTailApplication.kt            # Hilt Application 클래스
├── MainActivity.kt                   # 메인 액티비티
│
├── data/                            # 데이터 레이어
│   ├── dto/                         # 데이터 전송 객체
│   ├── local/                       # 로컬 데이터 소스 (Assets, Room)
│   ├── remote/                      # 원격 데이터 소스 (Network, Worker)
│   ├── repository/                  # Repository 구현체
│   └── mapper/                      # 데이터 모델 매퍼
│
├── domain/                          # 도메인 레이어
│   ├── model/                       # 도메인 모델
│   ├── repository/                  # Repository 인터페이스
│   └── usecase/                     # 비즈니스 로직
│
├── presentation/                    # 프레젠테이션 레이어
│   ├── splash/                      # 스플래시 화면
│   ├── bookshelf/                   # 책장 화면
│   ├── book/                        # 책 읽기 화면
│   ├── leveltest/                   # 레벨 테스트 화면

│   ├── setting/                     # 설정 화면
│   └── navigation/                  # 내비게이션 관리
│
├── di/                             # 의존성 주입 모듈
└── ui/                             # UI 테마 및 컴포넌트
```

## 기술적 특징

### 🏗️ 아키텍처 설계
- **Clean Architecture**: 계층 분리로 유지보수성 향상
- **MVVM 패턴**: 데이터 바인딩과 상태 관리 최적화
- **의존성 주입**: Hilt를 통한 모듈화된 컴포넌트 관리
- **단방향 데이터 플로우**: UI 상태의 예측 가능한 관리

### 📱 UI/UX 최적화
- **Jetpack Compose**: 선언형 UI로 직관적인 화면 구성
- **Material Design 3**: 최신 디자인 시스템 적용
- **반응형 레이아웃**: 다양한 화면 크기 지원
- **접근성 고려**: 시각 장애인을 위한 TTS 기능

### 🔊 오디오 시스템
- **SoundEffectManager**: 효과음 통합 관리
- **MediaPlayer 최적화**: 메모리 효율적인 오디오 재생
- **볼륨 차등 적용**: 효과음별 적절한 볼륨 설정
- **TTS 엔진**: 다국어 음성 합성 지원

### 🌐 다국어 지원
- **LanguageManager**: 동적 언어 전환
- **LocalizedText**: 컨텍스트 기반 텍스트 현지화
- **문자열 리소스**: 테툼어, 한국어, 영어 완전 지원

### 📊 데이터 관리
- **Room Database**: 오프라인 데이터 저장
- **GitHub API**: 원격 콘텐츠 관리
- **WorkManager**: 안정적인 백그라운드 다운로드
- **캐시 시스템**: 효율적인 이미지 로딩

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
- SiYoon-Lee - Environmental storybook creation
- Francisco Mendosa - English/Tetum translation

## 기획 및 발표 자료
- [2025 새싹 해커톤 AI 기능 추가 기획 자료](./docs/README.md)

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
