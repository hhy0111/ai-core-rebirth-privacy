# 03. Android Technical Plan

문서 담당 범위: Dev Agent A + Dev Agent B + Dev Lead

전제:
- Android 전용 Google Play 출시를 목표로 한다.
- 개인 개발자 유지보수성을 우선하여 Kotlin + Jetpack Compose/Canvas 기반 네이티브 Android를 기본안으로 한다.
- 서버, 랭킹, 길드, PvP, 멀티플레이, 대형 계정 시스템은 MVP 범위에서 제외한다.
- 광고와 결제는 진행을 막는 장치가 아니라 시간 절약, 편의, 외형 중심으로 연결한다.

---

## 11. 개발 구조 설계

* 담당 에이전트: Dev Agent A, Dev Agent B, Dev Lead
* 목표:
  - Android 전용 2D 방치형 하이브리드 캐주얼 게임을 개인 개발자가 실제 구현, 테스트, 출시, 유지보수할 수 있는 구조로 정의한다.
  - 핵심 루프, 저장 데이터, 오프라인 보상, 광고 보상, Google Play Billing, 오류 처리를 한 앱 모듈 안에서 단순하게 분리한다.
* 구현/기획 범위:
  - 기본 기술 스택:
    - 언어: Kotlin
    - UI: Jetpack Compose
    - 2D 렌더링: Compose `Canvas`, `DrawScope`, `graphicsLayer`
    - 상태 관리: ViewModel + StateFlow + 단방향 이벤트 처리
    - 비동기 처리: Kotlin Coroutines
    - 저장: Proto DataStore 중심, 앱 전용 백업 파일 1개 보조
    - 정적 콘텐츠: `assets/content/*.json` + kotlinx.serialization
    - 광고: Google Mobile Ads SDK Rewarded Ads
    - 결제: Google Play Billing Library 8.x 이상
    - 백그라운드 재시도: WorkManager
    - 테스트: JUnit, Turbine 또는 coroutine test, Compose UI Test, fake Ad/Billing adapters
  - 아키텍처:
    - MVP는 단일 Gradle 모듈 `:app`으로 시작한다.
    - 패키지 단위로 UI, domain, data, monetization을 분리한다.
    - 게임 수치 계산은 Android 의존성이 없는 순수 Kotlin 클래스로 유지하여 단위 테스트를 쉽게 한다.
    - 서버 검증, 서버 저장, 계정 동기화는 제외한다. 결제 보안 한계는 로컬 idempotency, Play Billing 재조회, 환불/취소 재동기화로 완화한다.
  - 핵심 런타임 흐름:
    - 앱 시작 시 저장 데이터 로드
    - 정적 콘텐츠 로드
    - 저장 데이터 schema migration
    - 결제 보유 상품 재조회
    - 오프라인 보상 계산
    - 메인 게임 화면 진입
    - foreground tick으로 자동 생산 반영
    - 중요 변경 시 debounce 저장
  - 제외 범위:
    - 서버 API, 클라우드 저장, 소셜 로그인, 랭킹, 길드, PvP, 실시간 이벤트 운영 도구, 복잡한 라이브옵스 CMS
* 산출물:
  - 제안 프로젝트 구조:

```text
app/
  build.gradle.kts
  src/main/
    AndroidManifest.xml
    assets/content/
      planets.json
      upgrades.json
      ai_profiles.json
      evolution_choices.json
      random_events.json
      products.json
    java/com/company/aicoreplanet/
      App.kt
      MainActivity.kt
      core/
        clock/GameClock.kt
        model/GameState.kt
        model/ResourceWallet.kt
        model/PlanetModels.kt
        model/UpgradeModels.kt
        model/AiModels.kt
        model/EventModels.kt
        model/MonetizationModels.kt
        result/GameResult.kt
      data/
        content/ContentRepository.kt
        content/JsonContentLoader.kt
        save/GameSave.proto
        save/SaveEnvelope.kt
        save/SaveRepository.kt
        save/SaveCorruptionRecovery.kt
        save/SaveMigration.kt
      domain/
        loop/GameLoopController.kt
        production/ProductionCalculator.kt
        upgrade/UpgradeService.kt
        planet/PlanetProgressService.kt
        planet/PlanetTravelService.kt
        offline/OfflineRewardCalculator.kt
        ai/AiPersonalityService.kt
        ai/AiMessageService.kt
        evolution/EvolutionService.kt
        event/EventService.kt
        prestige/PrestigeService.kt
      monetization/
        ads/RewardedAdManager.kt
        ads/AdRewardService.kt
        ads/AdRewardLedger.kt
        billing/BillingClientFacade.kt
        billing/ProductCatalog.kt
        billing/PurchaseProcessor.kt
        billing/EntitlementStore.kt
        billing/BillingRetryWorker.kt
      ui/
        navigation/AppNav.kt
        game/MainGameScreen.kt
        game/PlanetCanvas.kt
        game/ResourceTopBar.kt
        game/BottomActionBar.kt
        upgrade/UpgradeSheet.kt
        travel/GalaxyMapScreen.kt
        ai/AiPanel.kt
        event/EventDialog.kt
        shop/ShopScreen.kt
        common/UiEffects.kt
      qa/
        DebugGameMenu.kt
```

  - 책임 분리:

| 영역 | 주요 책임 | 실패 처리 기준 |
| --- | --- | --- |
| `domain/production` | foreground 자동 생산, 업그레이드 배율, 행성 단계 배율 계산 | 계산 예외 시 해당 tick 무시, 저장 데이터는 변경하지 않음 |
| `domain/offline` | 앱 재진입 시 오프라인 보상 계산 | 시간 이상치 감지 시 보상 clamp, 사용자 진행 차단 금지 |
| `data/save` | Proto DataStore 저장, schema migration, 손상 복구 | primary save 실패 시 rolling backup 복구, 실패 시 신규 세이브 + 복구 안내 |
| `monetization/ads` | 보상형 광고 로드/표시/보상 지급 | 광고 로드/표시 실패 시 보상 미지급, 기본 보상은 보존 |
| `monetization/billing` | 상품 조회, 구매 플로우, 보유 상품 복원, ack/consume | 구매 상태가 `PURCHASED`일 때만 지급, ack/consume 실패는 WorkManager 재시도 |
| `ui/game` | 메인 행성 Canvas, 자원 UI, 하단 탭, 시트/다이얼로그 | UI 에러는 snackbar/dialog로 축소, 게임 상태 손상 금지 |

  - 저장 데이터 설계:
    - `GameSave.proto`는 플레이어 진행만 저장한다. 행성/업그레이드/이벤트 정의는 JSON 콘텐츠에서 다시 읽는다.
    - `SaveEnvelope` 필드:
      - `schemaVersion`
      - `saveRevision`
      - `createdAtWallMillis`
      - `lastSavedWallMillis`
      - `lastSavedElapsedMillis`
      - `lastKnownBootMarker`
      - `integrityHash`
      - `payload`
    - 저장 손상 복구:
      - Proto parse 실패, schema 불일치, integrity hash 불일치 시 corruption recovery로 진입한다.
      - 최근 정상 저장본을 앱 전용 백업 파일에 1개 유지한다.
      - primary 복구 실패 시 backup을 검증 후 복원한다.
      - primary와 backup 모두 실패하면 신규 세이브를 생성하되, `recoveryGrant`로 초반 진행 5분 상당의 에너지를 지급해 이탈을 줄인다.
      - 복구 이벤트는 로컬 로그에 남기고, 서버 전송은 MVP에서 제외한다.
    - migration 원칙:
      - `schemaVersion`은 증가만 허용한다.
      - 누락 필드는 기본값으로 채운다.
      - 제거된 콘텐츠 ID는 같은 계열의 fallback ID로 매핑한다.
      - migration 실패 시 이전 backup으로 되돌리고 신규 콘텐츠 로드를 중단한다.

  - 오프라인 보상 설계:
    - 백그라운드에서 생산을 계속 돌리지 않고, 앱 재진입 시 한 번 계산한다.
    - 기본 보상 시간 cap: 8시간
    - 초반 튜토리얼 cap: 2시간
    - 광고 2배 보상 cap: 기본 cap 안에서만 적용
    - 악용 완화:
      - `System.currentTimeMillis()`와 `SystemClock.elapsedRealtime()`를 함께 저장한다.
      - 같은 부팅 세션이면 `min(wallDelta, elapsedDelta + 5분)`만 인정한다.
      - elapsed 시간이 과거로 이동했거나 부팅 변경이 의심되면 `min(wallDelta, 2시간)`으로 fallback한다.
      - wall clock이 과거로 이동하면 오프라인 보상은 0으로 처리하고 저장 기준 시간만 갱신한다.
      - 반복 이상치가 감지되면 24시간 동안 광고 2배 오프라인 보상만 제한하고 기본 진행은 막지 않는다.

  - 광고 보상 설계:
    - 보상형 광고 placement:
      - 생산 가속 5분
      - 오프라인 보상 2배
      - 이벤트 보상 강화
      - 행성 복원 속도 증가
      - 무료 상자 또는 진화 선택 리롤
    - 지급 원칙:
      - `onUserEarnedReward` 수신 전에는 보상을 지급하지 않는다.
      - 광고 로드 실패, 표시 실패, 중도 이탈은 실패 UI만 표시하고 플레이어 자원은 차감하지 않는다.
      - `AdRewardLedger`에 `placement`, `attemptId`, `rewardType`, `grantedAt`을 기록하여 중복 지급을 막는다.
      - 광고 제거 상품 보유자는 강제 광고가 없으므로 "광고 보기" 버튼을 숨기거나 무료 보상 쿨다운으로 대체한다.

  - Google Play Billing 설계:
    - Billing Library는 2026년 출시 기준 8.x 이상을 기본으로 한다. 7.x는 2026-08-31 이후 신규 출시/업데이트 사용 제한에 걸릴 수 있으므로 기본안에서 제외한다.
    - `BillingClient`는 앱 프로세스에서 단일 인스턴스로 관리한다.
    - 상품 ID는 `ProductCatalog`와 Play Console 상품 ID를 1:1로 맞춘다.
    - MVP 상품 6종:
      - `remove_ads`: non-consumable
      - `starter_pack`: consumable
      - `ai_upgrade_pack`: non-consumable 또는 1회성 entitlement
      - `production_boost_pack`: consumable
      - `planet_skin_pack_01`: non-consumable
      - `ai_skin_pack_01`: non-consumable
    - 지급 원칙:
      - `PENDING` 구매는 지급하지 않고 대기 상태 UI만 표시한다.
      - `PURCHASED` 전환 후 entitlement를 먼저 로컬 저장하고, 이후 acknowledge 또는 consume을 호출한다.
      - acknowledge/consume 실패 시 `BillingRetryWorker`가 unique work로 재시도한다.
      - purchase token 기준으로 idempotency를 보장한다.
      - 앱 시작, 상점 진입, 구매 완료 callback마다 `queryPurchasesAsync()`로 보유 상품을 재동기화한다.
      - 서버 없는 MVP에서는 환불/취소 실시간 통지를 받을 수 없으므로, non-consumable은 재조회 결과를 기준으로 entitlement를 보정한다.

  - 오류/실패 처리 공통 규칙:
    - 저장 실패: 메모리 상태 유지, 사용자에게 짧은 재시도 안내, 다음 중요 액션 전 강제 저장 재시도
    - 콘텐츠 로드 실패: 마지막 정상 콘텐츠 fallback, 없으면 앱 진입 차단 후 재설치/업데이트 안내
    - 광고 실패: 보상 미지급, 플레이 흐름 복귀, 버튼 쿨다운 없음
    - 결제 연결 실패: 상점 상품 비활성화, "Play 결제 연결 실패" 안내, 재시도 버튼 제공
    - 구매 취소: 실패로 기록하지 않고 상점으로 복귀
    - 구매 pending: 보상 미지급, "결제 확인 중" 상태 표시
    - ack/consume 실패: 보상은 이미 저장했으면 회수하지 않고, 재시도 큐에 넣어 3일 내 처리
    - 예외 로깅: MVP는 로컬 ring buffer 로그만 제공, Crashlytics 도입은 출시 직전 선택 사항

  - 공식 기준 참고:
    - Compose Canvas: https://developer.android.com/develop/ui/compose/graphics/draw/overview
    - Proto DataStore: https://developer.android.com/topic/libraries/architecture/datastore
    - WorkManager: https://developer.android.com/guide/background/persistent/getting-started
    - AdMob Rewarded Ads: https://developers.google.com/admob/android/rewarded
    - Play Billing deprecation: https://developer.android.com/google/play/billing/deprecation-faq
    - Play Billing integration: https://developer.android.com/google/play/billing/integrate.html
* 발견된 문제:
  - 서버를 제외하면 결제 토큰의 완전한 서버 검증과 환불 실시간 반영은 제한된다.
  - 오프라인 보상은 기기 시간 조작에 취약할 수 있다.
  - Compose Canvas는 복잡한 파티클을 과도하게 넣으면 저가형 기기에서 프레임 저하가 발생할 수 있다.
  - 저장 데이터 손상은 방치형 게임에서 즉시 낮은 리뷰로 이어질 수 있다.
* 수정 지시:
  - 결제는 token idempotency, `queryPurchasesAsync()`, ack/consume 재시도로 방어하고 서버 검증은 post-MVP 옵션으로 문서화한다.
  - 오프라인 보상은 wall time과 elapsed time 교차 검증 및 cap으로 완화한다.
  - Canvas 이펙트는 행성, 에너지 입자, 워프 효과까지만 MVP에 포함하고, 화면당 particle budget을 제한한다.
  - 저장은 Proto DataStore + rolling backup + migration test를 필수로 한다.
* 성공 기준:
  - 1개 Android 앱 모듈로 MVP 핵심 루프를 구현할 수 있다.
  - 자원 생산, 업그레이드, 행성 단계, 이동, 오프라인 보상, 광고 보상, 결제가 서로 느슨하게 연결된다.
  - 저장 손상, 광고 실패, 결제 실패가 진행 불가 상태를 만들지 않는다.
  - 서버 없이도 Google Play MVP 출시가 가능한 수준의 실패 처리와 테스트 경로를 갖춘다.
* 검증 방법:
  - `ProductionCalculator`, `OfflineRewardCalculator`, `SaveMigration`, `PurchaseProcessor`, `AdRewardService` 단위 테스트를 작성한다.
  - 저장 손상 proto, schema downgrade, 누락 콘텐츠 ID, 시간 역행, 광고 실패, pending purchase, duplicate purchase callback을 fake로 재현한다.
  - 저가형 기준 기기에서 메인 화면 60fps 목표, 최소 30fps 방어를 확인한다.
  - Play Console 내부 테스트 트랙에서 AdMob 테스트 광고와 Billing license tester 구매를 검증한다.
* 승인 상태: Approved

---

## 12. 구현 계획 수립

* 담당 에이전트: Dev Agent A, Dev Agent B, Dev Lead
* 목표:
  - 실제 Android 구현 순서를 MVP 리스크가 낮은 방향으로 정렬한다.
  - 수치 계산과 저장 안정성을 먼저 고정한 뒤 UI, 광고, 결제를 붙인다.
* 구현/기획 범위:
  - 개발 원칙:
    - 먼저 domain 순수 로직을 완성하고 테스트한다.
    - UI는 game state를 표시하고 command를 보내는 얇은 계층으로 유지한다.
    - 광고/결제 SDK는 interface 뒤에 감싸 fake 구현으로 먼저 테스트한다.
    - 콘텐츠 수치는 JSON으로 분리하여 빌드 없이 조정 가능하게 한다.
    - 출시 전까지 서버 기능을 추가하지 않는다.
  - 구현 단계:

| 순서 | 담당 | 작업 | 완료 기준 |
| --- | --- | --- | --- |
| 1 | Dev Lead | Android 프로젝트 scaffold, Gradle Kotlin DSL, Compose 설정 | debug/release 빌드 성공 |
| 2 | Dev Agent A | core model, resource wallet, production calculator | 초당 생산량 단위 테스트 통과 |
| 3 | Dev Agent A | upgrade service, planet progress, planet travel | 업그레이드 20종과 행성 5개 진행 가능 |
| 4 | Dev Agent A | foreground game loop, tick throttling, save debounce | 앱 foreground 10분 실행 중 자원 오차 없음 |
| 5 | Dev Lead | Proto DataStore, save envelope, migration, backup recovery | 손상 save 복구 테스트 통과 |
| 6 | Dev Agent A | offline reward calculator | 시간 조작/재부팅/8시간 cap 테스트 통과 |
| 7 | Dev Agent B | AI personality/message service | 성향 3종 메시지와 진행도 반응 출력 |
| 8 | Dev Agent B | event service, evolution service | 이벤트 10종, 선택 3계열 적용 |
| 9 | Dev Agent A | Compose 메인 화면, PlanetCanvas, 업그레이드/이동 UI | 1~3분 세션에서 핵심 조작 가능 |
| 10 | Dev Agent B | RewardedAdManager fake -> AdMob test ID 연결 | 광고 실패/성공/중복 보상 테스트 통과 |
| 11 | Dev Agent B | BillingClientFacade fake -> Play Billing 연결 | license tester 구매/복원/pending/cancel 검증 |
| 12 | Dev Lead | lint, unit, instrumentation, release bundle, QA hooks | release AAB 생성 및 테스트 체크리스트 통과 |

* 산출물:
  - 구현 마일스톤:
    - M0: 프로젝트 생성 및 CI 없이 로컬 Gradle 명령 확정
    - M1: 순수 게임 엔진 완성
    - M2: 저장/오프라인 보상 완성
    - M3: Compose/Canvas 플레이 화면 완성
    - M4: AI/이벤트/진화 선택 완성
    - M5: 광고 보상 연결
    - M6: Billing 상품 연결
    - M7: QA 3회 및 Google Play 내부 테스트
  - 추천 Gradle 명령:

```powershell
.\gradlew :app:testDebugUnitTest
.\gradlew :app:connectedDebugAndroidTest
.\gradlew :app:lintDebug
.\gradlew :app:assembleDebug
.\gradlew :app:bundleRelease
```

  - 테스트 전략:
    - 단위 테스트:
      - 생산량 계산
      - 업그레이드 비용 증가
      - 행성 단계 전환
      - 진화 선택 누적 효과
      - 이벤트 결과
      - 오프라인 보상 cap
      - 저장 migration
      - 결제 token idempotency
    - UI 테스트:
      - 메인 화면 자원 UI 표시
      - 업그레이드 버튼 활성/비활성
      - 행성 단계 시각 상태 변경
      - 하단 탭 이동
      - 이벤트 다이얼로그 선택
      - 광고 실패 후 UI 복귀
      - 상점 상품 로드 실패 안내
    - 수동 QA:
      - 신규 설치
      - 앱 강제 종료 후 복귀
      - 기기 시간 앞/뒤 변경
      - 네트워크 끊김 상태 광고/결제 버튼
      - 결제 pending/cancel/test purchase
      - 저장 파일 손상 시나리오
      - 1~3분 세션 10회 반복
    - 성능 테스트:
      - 메인 Canvas particle 수 제한
      - recomposition count 확인
      - 저가형 기기에서 frame drop 확인
      - 앱 시작 3초 이내 메인 진입 목표
  - 빌드 설정 기준:
    - `minSdk`: 26 이상 권장
    - `targetSdk`: 2026-05-05 기준 Google Play 신규 앱 제출 요구인 Android 15 API 35 이상
    - `compileSdk`: 개발 시점 Android Studio 안정 채널의 최신 SDK
    - `versionCode`: 내부 테스트부터 단조 증가
    - `release`: R8 enabled, minify enabled, resource shrink enabled
    - `debug`: fake billing/fake ads 전환 가능
    - `internal`: Play test ad unit과 Play Billing license tester 검증용
  - 파일별 구현 우선순위:
    - 1순위: `core/model`, `domain/production`, `domain/upgrade`, `domain/planet`
    - 2순위: `data/save`, `domain/offline`
    - 3순위: `ui/game`, `ui/upgrade`, `ui/travel`
    - 4순위: `domain/ai`, `domain/event`, `domain/evolution`
    - 5순위: `monetization/ads`, `monetization/billing`
    - 6순위: `qa/DebugGameMenu`, release hardening
* 발견된 문제:
  - 광고와 결제를 먼저 붙이면 SDK 상태와 Play Console 설정 때문에 핵심 재미 검증이 지연된다.
  - 콘텐츠 JSON을 너무 자유롭게 만들면 migration과 밸런스 검증이 어려워진다.
  - 개인 개발자가 CI, 서버, 복잡한 모듈화를 동시에 운영하기 어렵다.
* 수정 지시:
  - 1차 구현은 fake ads/fake billing으로 완료하고, Play SDK 연결은 후반 통합 단계에서 진행한다.
  - 콘텐츠 JSON schema는 MVP 필드만 허용하고 unknown field는 무시하되 필수 ID 누락은 빌드 전 테스트에서 실패시킨다.
  - 모듈 분리는 패키지 단위로만 유지하고, 출시 후 유지보수 병목이 생길 때 Gradle 멀티 모듈을 검토한다.
  - 실패 시 수정 방향:
    - 생산 곡선이 지루하면 수치 JSON만 조정한다.
    - UI 성능이 낮으면 particle 수와 blur/glow 레이어를 줄인다.
    - 결제 QA가 불안정하면 consumable 상품을 후순위로 미루고 non-consumable부터 출시한다.
* 성공 기준:
  - M1 완료 시 광고/결제 없이도 5개 행성 반복 루프가 동작한다.
  - M2 완료 시 앱 종료/복귀/시간 cap/저장 복구가 테스트로 보장된다.
  - M5 완료 시 광고 실패가 보상 손실 또는 진행 막힘으로 이어지지 않는다.
  - M6 완료 시 구매, 복원, pending, 취소, 중복 callback이 모두 안전하게 처리된다.
  - M7 완료 시 release AAB를 Play Console 내부 테스트에 업로드할 수 있다.
* 검증 방법:
  - 각 마일스톤 종료 시 `testDebugUnitTest`를 통과해야 다음 단계로 이동한다.
  - M3 이후 매 빌드마다 실제 기기에서 3분 플레이 세션을 기록한다.
  - M5/M6는 반드시 Play Console 내부 테스트 또는 license tester 환경에서 검증한다.
  - QA Lead가 지정한 3회 QA 사이클 결과에서 진행 불가, 저장 손실, 결제/광고 보상 오류가 없어야 한다.
* 승인 상태: Approved

---

## 20. Google Play 출시 체크리스트 작성

* 담당 에이전트: Dev Agent A, Dev Agent B, Dev Lead
* 목표:
  - Google Play Android 전용 출시 전에 기술, 정책, 광고, 결제, 저장 안정성, QA 조건을 확인한다.
  - 출시 차단 이슈를 사전에 분리하여 개인 개발자가 처리 가능한 순서로 정리한다.
* 구현/기획 범위:
  - Android 빌드/서명
  - Google Play Console 앱 설정
  - AdMob 보상형 광고 설정
  - Google Play Billing 상품 설정
  - 데이터 보안/개인정보/광고 선언
  - 내부 테스트/비공개 테스트/프로덕션 출시
  - 저장 데이터 손상 복구, 오프라인 보상 악용 완화, 광고/결제 실패 처리 최종 검증
* 산출물:
  - 출시 전 필수 체크리스트:

| 분류 | 체크 항목 | 승인 기준 |
| --- | --- | --- |
| 빌드 | `bundleRelease`로 AAB 생성 | release AAB 생성 성공 |
| 빌드 | `targetSdk` 최신 Play 요구 충족 | 2026-05-05 기준 API 35 이상, 제출 직전 재확인 |
| 빌드 | `versionCode` 단조 증가 | 내부/비공개/프로덕션 트랙 충돌 없음 |
| 서명 | Play App Signing 활성화 | 업로드 키 분리 보관 |
| 서명 | release keystore 백업 | 암호와 키 파일을 별도 안전 저장 |
| 저장 | 신규 설치, 업데이트 설치, 삭제 후 재설치 확인 | 진행 불가 없음 |
| 저장 | 손상 save 복구 테스트 | primary 실패 시 backup 복원, 둘 다 실패 시 신규 세이브 진입 |
| 저장 | schema migration 테스트 | 이전 save가 최신 앱에서 정상 로드 |
| 오프라인 | 8시간 cap 확인 | cap 초과 지급 없음 |
| 오프라인 | 시간 역행/미래 이동 테스트 | 비정상 보상 clamp, 계정 차단 없음 |
| 광고 | 테스트 광고 ID가 release에서 제거됨 | 실제 AdMob unit ID 연결 |
| 광고 | 광고 로드 실패 처리 | 기본 플레이 흐름 유지 |
| 광고 | `onUserEarnedReward` 전 보상 미지급 | 중복 지급 없음 |
| 광고 | 광고 제거 상품 보유 시 광고 UI 처리 | 강제 광고 없음 |
| 결제 | Play Console 상품 6종 활성화 | 앱 상품 ID와 Console ID 일치 |
| 결제 | license tester 구매 성공 | 구매 후 entitlement 저장 |
| 결제 | pending/cancel/refund 유사 시나리오 | 지급 조건과 복원 처리 정상 |
| 결제 | acknowledge/consume 재시도 | 3일 내 ack/consume 실패 방지 큐 존재 |
| 결제 | 앱 시작 시 구매 복원 | non-consumable entitlement 보정 |
| QA | QA 1차, 2차, 3차 완료 | 진행 불가/저장 손실/광고 결제 보상 오류 0건 |
| 성능 | 메인 화면 프레임 확인 | 저가형 30fps 이상, 목표 60fps |
| 성능 | 앱 시작 시간 확인 | 일반 기기 3초 이내 메인 진입 목표 |
| 정책 | 개인정보처리방침 URL 등록 | Data Safety 내용과 불일치 없음 |
| 정책 | Data Safety form 작성 | 광고 SDK, 결제 SDK 데이터 처리 반영 |
| 정책 | 광고 포함 여부 선언 | AdMob 사용 사실 반영 |
| 정책 | 콘텐츠 등급 설문 완료 | 게임 내용과 일치 |
| 정책 | 대상 연령 및 Families 여부 설정 | 타겟 유저와 광고 설정 일치 |
| 정책 | 앱 액세스 권한 제출 | 로그인 없음으로 명시 |
| 테스트 | 내부 테스트 트랙 업로드 | 설치/결제/광고 기본 검증 |
| 테스트 | 신규 개인 개발자 계정 요건 확인 | 해당 시 12명 이상 14일 연속 비공개 테스트 |
| 스토어 | 앱 이름, 짧은 설명, 긴 설명 | 게임 핵심 컨셉 전달 |
| 스토어 | 스크린샷, 아이콘, feature graphic | 행성 변화와 AI 캐릭터가 첫 화면에서 보임 |
| 출시 | staged rollout 계획 | 5% -> 25% -> 100% 순차 확대 |
| 출시 | 출시 후 모니터링 | crashes, ANR, 결제 실패 리뷰 매일 확인 |

  - 출시 차단 이슈 정의:
    - 저장 데이터 손실 또는 복구 실패
    - 오프라인 보상 무제한 획득
    - 광고 시청 후 보상 미지급
    - 광고 미시청 상태에서 보상 지급
    - 구매 후 상품 미지급
    - 구매 취소/pending에서 상품 지급
    - 앱 시작 crash
    - 메인 루프 진행 불가
    - Play Console 정책 필수 항목 미작성
  - 공식 기준 참고:
    - Target API level requirements: https://support.google.com/googleplay/android-developer/answer/11926878
    - Android App Bundle: https://developer.android.com/guide/app-bundle/faq
    - Play App Signing: https://support.google.com/googleplay/android-developer/answer/9842756
    - Play testing requirements: https://support.google.com/googleplay/android-developer/answer/14151465
    - Data safety form: https://support.google.com/googleplay/android-developer/answer/10787469
    - One-time purchase lifecycle: https://developer.android.com/google/play/billing/lifecycle/one-time
    - AdMob Rewarded Ads: https://developers.google.com/admob/android/rewarded
* 발견된 문제:
  - Google Play target API, Billing Library 지원 버전, 개인 개발자 계정 테스트 요건은 시점에 따라 변경된다.
  - 신규 개인 개발자 계정이면 14일 비공개 테스트 요건 때문에 출시 일정이 최소 2주 이상 밀릴 수 있다.
  - 서버 없는 결제 구조는 환불/취소 실시간 반영에 한계가 있다.
  - 개인정보처리방침과 Data Safety form이 광고 SDK/결제 SDK 데이터 처리와 불일치하면 심사 반려 가능성이 있다.
* 수정 지시:
  - 제출 2주 전 Play Console 정책 페이지에서 target API, Billing Library, 테스트 요건을 다시 확인한다.
  - 신규 개인 개발자 계정이면 내부 QA와 별도로 12명 이상 비공개 테스트 모집을 즉시 시작한다.
  - Billing은 release 전 license tester로 pending, 취소, 구매 복원, 중복 callback을 반드시 검증한다.
  - AdMob은 release 빌드에서 테스트 광고 ID가 남아 있지 않은지 빌드 설정으로 검사한다.
  - Data Safety는 AdMob, Play Billing, Crash reporting 도입 여부를 기준으로 다시 작성한다.
* 성공 기준:
  - release AAB가 Play Console 내부 테스트에 업로드된다.
  - 저장 손상 복구, 광고 보상, 결제 지급/복원, 오프라인 보상 cap 테스트가 통과된다.
  - Google Play 필수 정책 항목이 모두 작성된다.
  - QA 3회 후 출시 차단 이슈가 0건이다.
  - staged rollout을 시작할 수 있는 상태다.
* 검증 방법:
  - 로컬: `testDebugUnitTest`, `connectedDebugAndroidTest`, `lintDebug`, `bundleRelease`
  - Play Console: 내부 테스트 설치, pre-launch report, device catalog, app content 항목, monetization 상품 상태 확인
  - AdMob: test device 등록 후 reward callback 확인, release 전 실제 unit ID 확인
  - Billing: license tester 구매, 구매 복원, pending/cancel test instrument, acknowledge/consume retry 로그 확인
  - 저장/오프라인: QA save corruption fixture, 시간 변경 수동 테스트, 앱 강제 종료/재시작 반복 테스트
* 승인 상태: Approved
