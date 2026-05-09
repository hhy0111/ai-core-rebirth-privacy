# Google Play Android Release Checklist

확인일: 2026-05-05  
상태: 출시 준비용 체크리스트 초안. 실제 제출 직전 공식 문서 재확인 필요.

## 20. Google Play 출시 체크리스트 작성

* 담당 에이전트: Dev Lead, QA Lead, Communication Agent, Command Agent / PM
* 목표: Android 전용 Google Play 출시 전에 빌드, 정책, 광고, 결제, 개인정보, QA 차단 조건을 확인한다.
* 구현/기획 범위:
  * 새 앱/업데이트는 Android 15, API 35 이상 target 필요.
  * Google Play 새 앱은 Android App Bundle로 배포.
  * Google Play Billing Library는 8.x 사용을 기준으로 한다.
  * Google Mobile Ads SDK는 최신 버전 사용을 기준으로 한다.
  * Android 15+ 대상 앱은 16KB 페이지 크기 호환성을 확인한다.
  * Data safety form과 privacy policy를 제출 전 준비한다.
* 산출물:

| 영역 | 현재 기준 | 프로젝트 상태 | 출시 전 조치 |
|---|---:|---|---|
| Target SDK | API 35 이상 | `targetSdk = 35` | 제출 직전 최신 요구 재확인 |
| Compile SDK | API 35 | `compileSdk = 35` | SDK 36 요구가 생기면 상향 |
| Build tools | Kotlin 2.2는 AGP 8.10 이상 필요 | Gradle 8.11.1 wrapper, AGP 8.10.1, Kotlin 2.2.10 | Android Studio/CI도 같은 wrapper 사용 |
| 배포 형식 | AAB | Gradle app bundle 가능 구조 | `bundleRelease` 산출물 서명 |
| Billing | Library 8.x | `billing-ktx:8.3.0` 의존성 추가 | 실제 BillingClient 구현, 상품 등록, 구매 복원 |
| AdMob | 최신 SDK | `play-services-ads:25.2.0` 의존성 및 실제 앱 ID/광고 단위 ID 적용 | 내부 테스트에서 로드/시청/보상 검증 |
| 16KB page size | Android 15+ 대상 앱 필수 | 네이티브 `.so` 미사용 | 의존 SDK 네이티브 라이브러리 포함 여부 검사 |
| 개인정보 | Data safety + privacy policy | 문서만 있음 | 정책 URL, 수집 데이터, 광고 ID 사용 고지 |
| 저장 데이터 | 손상 복구 필요 | JSON + 백업 저장 초안 | 마이그레이션/복구 테스트 추가 |
| QA | 3회 이상 | 단위 테스트 초안 | 기기 테스트와 릴리즈 QA 필요 |

* 발견된 문제:
  * AdMob SDK 연결 코드와 실제 앱 ID/광고 단위 ID는 적용되었으나 내부 테스트 기기에서 광고 로드/보상 검증이 필요하다.
  * Play Console 상품 등록과 실제 결제 테스트가 필요하다.
  * 서버 검증이 없는 MVP 클라이언트 결제 처리이므로 규모가 커지면 Play Developer API 기반 서버 검증이 필요하다.
  * Play Console 등록 정보, 개인정보 처리방침, 스크린샷, 그래픽 자산이 아직 없다.
  * 최종 QA 3회 사이클은 아직 실행되지 않았다.
* 수정 지시:
  * 실제 광고 ID 테스트 전 AdMob 테스트 기기를 등록하고 내부 테스트 트랙에서 보상 지급을 확인한다.
  * Play Console에 `docs/monetization/PLAY_CONSOLE_ADMOB_PREP.md`의 상품 ID를 그대로 등록한다.
  * 저장 포맷 버전과 복구 테스트를 추가한다.
  * 내부 테스트 트랙에서 광고/결제/저장/오프라인 보상 회귀 테스트를 수행한다.
* 성공 기준:
  * Play Console 내부 테스트 업로드 성공.
  * 광고 보상을 받지 못한 경우 자원을 지급하지 않고 명확한 실패 메시지를 표시.
  * 결제 성공 후 권한 지급, 실패/취소 후 미지급, 재설치 후 구매 복원 성공.
  * 저장 데이터 손상 시 백업 또는 초기 상태로 복구되고 진행 불가가 발생하지 않음.
  * QA 1차, 2차, 3차 Blocker 0개.
* 검증 방법:
  * `./gradlew testDebugUnitTest`
  * `./gradlew bundleRelease`
  * Android Studio/Gradle로 내부 테스트용 signed AAB 생성.
  * Play Console internal testing 업로드.
  * 실제 기기 최소 3종에서 1~3분 세션, 오프라인 보상, 광고/결제 실패 테스트.
* 승인 상태: Rejected until release SDK flows and QA cycles are complete

## 공식 기준 출처

* Target API level: https://developer.android.com/google/play/requirements/target-sdk
* Android App Bundle: https://developer.android.com/guide/app-bundle
* Google Play Billing release notes: https://developer.android.com/google/play/billing/release-notes
* Billing deprecation timeline: https://developer.android.com/google/play/billing/deprecation-faq
* Google Mobile Ads SDK: https://developers.google.com/admob/android/sdk
* 16KB page size: https://developer.android.com/guide/practices/page-sizes
* Data safety: https://support.google.com/googleplay/android-developer/answer/10787469
* Kotlin/AGP/D8/R8 compatibility: https://developer.android.com/build/kotlin-support
